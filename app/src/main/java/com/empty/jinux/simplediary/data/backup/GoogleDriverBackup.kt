package com.empty.jinux.simplediary.data.backup

import android.content.IntentSender
import androidx.fragment.app.Fragment
import android.util.Log
import android.widget.Toast
import com.empty.jinux.baselibaray.log.logd
import com.empty.jinux.simplediary.data.source.local.room.DATABASE_NAME
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.drive.*
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.SearchableField
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.common.io.ByteStreams
import com.google.common.io.Files
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Set a String value in the preferences editor, to be written back once
 * {@link #commit} or {@link #apply} are called.
 *
 * @param key The name of the preference to modify.
 * @param value The new value for the preference.  Passing {@code null}
 *    for this argument is equivalent to calling {@link #remove(String)} with
 *    this key.
 *
 * @return Returns a reference to the same Editor object, so you can
 * chain put calls together.
 */
class GoogleDriverBackup(val fragment: Fragment) : Backup() {
    val activity = fragment.activity!!

    override val needLogin: Boolean = true

    override fun login(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account == null) {
            signIn()
            return false
        } else {
            //Initialize the drive api
            mDriveClient = Drive.getDriveClient(activity, account)
            // Build a drive resource client.
            mDriveResourceClient = Drive.getDriveResourceClient(activity, account)
            return true
        }
    }

    override fun importDb(path: String) {

    }

    override fun backupDb(out: String) {
        mDriveResourceClient?.apply {
            val children = listChildren(rootFolder.result).result
            val backupFolder = children.filter { it.title == BACKUP_FOLDER }.get(0)
            if (backupFolder == null) {
//                backupFolder = createFolder(rootFolder.result, MetadataChangeSet().apply {  })
            } else {
                createContents().continueWith {
                    it.result.outputStream.bufferedWriter().apply { write("hello");close() };

                    createFile(backupFolder.driveId.asDriveFolder(), MetadataChangeSet.Builder().apply {
                        setTitle(out)
                        setMimeType("application/db")
                    }.build(), it.result)
                }

            }
        }
    }


    private var mDriveClient: DriveClient? = null
    private var mDriveResourceClient: DriveResourceClient? = null

    lateinit var mOpenItemTaskSource: TaskCompletionSource<DriveId>

    private fun connectToDrive(backup: Boolean) {
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account == null) {
            signIn()
        } else {
            //Initialize the drive api
            mDriveClient = Drive.getDriveClient(activity, account)
            // Build a drive resource client.
            mDriveResourceClient = Drive.getDriveResourceClient(activity, account)
            if (backup)
                startDriveBackup()
            else
                startDriveRestore()
        }
    }

    private fun signIn() {
        logd("Start sign in", TAG)
        val googleSignInClient = buildGoogleSignInClient()
        fragment.startActivityForResult(googleSignInClient.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun buildGoogleSignInClient(): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE)
                .build()
        return GoogleSignIn.getClient(activity, signInOptions)
    }


    private fun startDriveBackup() {
        mDriveResourceClient!!
                .createContents()
                .continueWithTask { task -> createFileIntentSender(task.result) }
                .addOnSuccessListener {
                    fragment.activity?.toast("hello successfully")
                }.addOnFailureListener {
                    fragment.activity?.toast("hello failed ${Log.getStackTraceString(it)}")
                }
    }


    private fun createFileIntentSender(driveContents: DriveContents): Task<IntentSender> {

        val inFileName = activity.getDatabasePath(DATABASE_NAME).toString()

        try {
            val outputStream = driveContents.outputStream
            Files.copy(File(inFileName), outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val metadataChangeSet = MetadataChangeSet.Builder()
                .setTitle("database_backup.db")
                .setMimeType("application/db")
                .build()


        val createFileActivityOptions = CreateFileActivityOptions.Builder()
                .setInitialMetadata(metadataChangeSet)
                .setInitialDriveContents(driveContents)
                .build()

        return mDriveClient!!
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith {
                    fragment.startIntentSenderForResult(
                            it.result, REQUEST_CODE_CREATION, null, 0, 0, 0, null)
                    it.result
                }
    }


    private fun startDriveRestore() {
        pickFile()
                .addOnSuccessListener(activity
                ) { driveId -> retrieveContents(driveId.asDriveFile()) }
                .addOnFailureListener(activity) { e -> Log.e(TAG, "No file selected", e) }
    }

    private fun retrieveContents(file: DriveFile) {

        //DB Path
        val inFileName = activity.getDatabasePath(DATABASE_NAME).toString()

        val openFileTask = mDriveResourceClient!!.openFile(file, DriveFile.MODE_READ_ONLY)

        openFileTask
                .continueWithTask { task ->
                    val contents = task.result
                    try {
                        val parcelFileDescriptor = contents.parcelFileDescriptor
                        val fileInputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)

                        // Open the empty db as the output stream
                        val output = FileOutputStream(inFileName)

                        try {
                            ByteStreams.copy(fileInputStream, output)
                        } finally {
                            output.flush()
                            output.close()
                            fileInputStream.close()
                        }
                        Toast.makeText(activity, "Import completed", Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(activity, "Error on import", Toast.LENGTH_SHORT).show()
                    }

                    task
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Unable to read contents", e)
                    Toast.makeText(activity, "Error on import", Toast.LENGTH_SHORT).show()
                }
    }

    private fun pickItem(openOptions: OpenFileActivityOptions): Task<DriveId> {

        mDriveClient!!
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith { task ->
                    fragment.startIntentSenderForResult(
                            task.result, REQUEST_CODE_OPENING, null, 0, 0, 0, null)
                }

        mOpenItemTaskSource = TaskCompletionSource()
        return mOpenItemTaskSource.task
    }

    private fun pickFile(): Task<DriveId> {
        val openOptions = OpenFileActivityOptions.Builder()
                .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "application/db"))
                .setActivityTitle("Select DB File")
                .build()
        return pickItem(openOptions)
    }

    companion object {
        const val REQUEST_CODE_OPENING: Int = 234
        const val REQUEST_CODE_CREATION: Int = 238
        const val REQUEST_CODE_SIGN_IN: Int = 239


        private val TAG = "Google Drive Activity"
    }


}