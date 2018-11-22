package com.empty.jinux.simplediary.data.backup

interface BackupIOPath {
    fun getOriginPath(): String
    fun getBackupPath(): String
}