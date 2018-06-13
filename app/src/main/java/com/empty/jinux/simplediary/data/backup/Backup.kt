package com.empty.jinux.simplediary.data.backup

interface Backup {
    fun performBackup(outFileName: String)
    fun performImport(inFileName: String)
}