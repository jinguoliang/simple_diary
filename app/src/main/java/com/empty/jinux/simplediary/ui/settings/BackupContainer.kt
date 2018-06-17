package com.empty.jinux.simplediary.ui.settings

import com.empty.jinux.simplediary.data.backup.Backup
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.di.Remote
import javax.inject.Inject

class BackupContainer
@Inject internal constructor(@param:Local val local: Backup,
                     @param:Remote val remote: Backup)