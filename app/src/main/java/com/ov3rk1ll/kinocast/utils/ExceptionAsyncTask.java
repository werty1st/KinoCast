package com.ov3rk1ll.kinocast.utils;

import android.os.AsyncTask;

public abstract class ExceptionAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private Exception exception=null;
    private Params[] params;

    @Override
    final protected Result doInBackground(Params... params) {
        try {
            this.params = params;
            return doInBackground();
        }
        catch (Exception e) {
            exception = e;
            return null;
        }
    }

    abstract protected Result doInBackground() throws Exception;

    public Params[] getParams() {
        return params;
    }
    public Exception getException() {
        return exception;
    }

}
