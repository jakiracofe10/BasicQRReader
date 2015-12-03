package com.example.jason.basicqrreader;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Jason on 12/3/2015.
 */
public class Networking {

    protected static final int GROUPCAST_PORT = 20000;
    protected static final String GROUPCAST_SERVER = "ec2-52-27-83-175.us-west-2.compute.amazonaws.com";

    Socket socket = null;
    BufferedReader in = null;
    PrintWriter out = null;
    boolean connected = false;

    void connect() {
        new AsyncTask<Void, Void, String>() {

            String errorMsg = null;

            @Override
            protected String doInBackground(Void... args) {
                try {
                    connected = false;
                    socket = new Socket(GROUPCAST_SERVER, GROUPCAST_PORT);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream());

                    connected = true;

                } catch (UnknownHostException e1) {
                    errorMsg = e1.getMessage();
                } catch (IOException e1) {
                    errorMsg = e1.getMessage();
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
                return errorMsg;
            }

            @Override
            protected void onPostExecute(String errorMsg) {
                if (errorMsg == null) {
                    receive();
                }

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    void receive() {
        AsyncTask<Void, String, Void> voidStringVoidAsyncTask = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... args) {
                try {
                    while (connected) {
                        String msg = in.readLine();
                        if (msg == null) {
                            break;
                        }
                        publishProgress(msg);
                    }
                } catch (UnknownHostException e1) {
                } catch (IOException e1) {
                } finally {
                    connected = false;
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... lines) {
                String msg = lines[0];
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    void disconnect() {
        new Thread() {
            @Override
            public void run() {
                if (connected) {
                    connected = false;
                }
                if (out != null) {
                    out.print("BYE");
                    out.flush();
                    out.close();
                }
                if (socket != null)
                    try { socket.close();} catch(IOException ignored) {}
            }
        }.start();
    }

    boolean send(String msg) {
        if (!connected) {
            return false;
        }

        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... msg) {
                out.println(msg[0]);
                return out.checkError();
            }

            @Override
            protected void onPostExecute(Boolean error) {

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,msg);

        return true;
    }
}