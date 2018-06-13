package com.empty.jinux.simplediary.data.backup

import android.support.v4.app.Fragment
import android.util.Log
import android.widget.Toast
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
class GoogleDriverBackup(val fragment: Fragment) : Backup {
    val activity = fragment.activity!!

    override fun performBackup(outFileName: String) {
        connectToDrive(true)
    }

    override fun performImport(inFileName: String) {
        connectToDrive(false)
    }


    private var mDriveClient: DriveClient? = null
    private var mDriveResourceClient: DriveResourceClient? = null

    lateinit var mOpenItemTaskSource: TaskCompletionSource<DriveId>

    fun connectToDrive(backup: Boolean) {
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
        Log.i(TAG, "Start sign in")
        val GoogleSignInClient = buildGoogleSignInClient()
        fragment.startActivityForResult(GoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN)
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
                .continueWithTask(
                        { task -> createFileIntentSender(task.result) })
                .addOnFailureListener(
                        { e -> Log.w(TAG, "Failed to create new contents.", e) })
    }


    private fun createFileIntentSender(driveContents: DriveContents): Task<Void> {

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
                .continueWith(
                        { task ->
                            fragment.startIntentSenderForResult(task.result, REQUEST_CODE_CREATION, null, 0, 0, 0, null)
                            null
                        })
    }


    private fun startDriveRestore() {
        pickFile()
                .addOnSuccessListener(activity
                ) { driveId -> retrieveContents(driveId.asDriveFile()) }
                .addOnFailureListener(activity, { e -> Log.e(TAG, "No file selected", e) })
    }

    private fun retrieveContents(file: DriveFile) {

        //DB Path
        val inFileName = activity.getDatabasePath(DATABASE_NAME).toString()

        val openFileTask = mDriveResourceClient!!.openFile(file, DriveFile.MODE_READ_ONLY)

        openFileTask
                .continueWithTask({ task ->
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
                })
                .addOnFailureListener({ e ->
                    Log.e(TAG, "Unable to read contents", e)
                    Toast.makeText(activity, "Error on import", Toast.LENGTH_SHORT).show()
                })
    }

    private fun pickItem(openOptions: OpenFileActivityOptions): Task<DriveId> {
        mOpenItemTaskSource = TaskCompletionSource()
        mDriveClient!!
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith { task ->
                    fragment.startIntentSenderForResult(
                            task.result, REQUEST_CODE_OPENING, null, 0, 0, 0, null)
                }
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