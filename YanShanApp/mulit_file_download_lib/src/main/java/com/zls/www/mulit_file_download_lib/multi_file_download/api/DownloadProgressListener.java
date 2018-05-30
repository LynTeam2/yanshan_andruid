package com.zls.www.mulit_file_download_lib.multi_file_download.api;

public interface DownloadProgressListener {
    /**
     * 下载进度
     * @param read
     * @param count
     * @param done
     */
    void update(long read, long count, boolean done);
}
