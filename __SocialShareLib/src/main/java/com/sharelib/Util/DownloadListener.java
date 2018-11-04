package com.sharelib.Util;

import java.io.File;

public abstract class DownloadListener {
    protected abstract void onComplete(File[] file);
    protected abstract void onDownloadError(Exception e);
}
