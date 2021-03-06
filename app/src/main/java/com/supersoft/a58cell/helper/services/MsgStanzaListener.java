package com.supersoft.a58cell.helper.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.supersoft.a58cell.R;
import com.supersoft.a58cell.helper.util.Constant;
import com.supersoft.a58cell.helper.util.DBHelper;
import com.supersoft.a58cell.helper.util.Session;
import com.supersoft.a58cell.model.ChatGroupDetail;
import com.supersoft.a58cell.model.ChatGroupModel;
import com.supersoft.a58cell.model.ChatSingleModel;
import com.supersoft.a58cell.model.InfoItem;
import com.supersoft.a58cell.model.InfoTipe;
import com.supersoft.a58cell.model.LikestatusModel;
import com.supersoft.a58cell.model.Loghistory;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

/**
 * Created by itclub21 on 1/6/2018.
 */

public class MsgStanzaListener implements StanzaListener {
    private static final String TAG = "MsgStanzaListener";
    Context mApplicationContext;
    Session _session;

    public MsgStanzaListener(Context context)
    {
        this.mApplicationContext = context;
        _session = new Session(this.mApplicationContext);
    }
    @Override
    public void processStanza(Stanza stanza) throws SmackException.NotConnectedException, InterruptedException {
        //Log.e(TAG, "processStanza " + stanza.toXML().toString());
        //Log.e(TAG, "receive message: " + stanza.getFrom() + " to " + stanza.getTo());
        Message smackMessage = (Message) stanza;
        String address = ""+smackMessage.getFrom();
        String body = smackMessage.getBody();
        Message.Type type = smackMessage.getType();

        if (smackMessage.getError() != null) {
            //  smackMessage.getError().getCode();
            String error = "Error " + smackMessage.getError() + " (" + smackMessage.getError().getCondition() + "): " + smackMessage.getError().getConditionText();
            Log.e(TAG, error);
            return;
        }

        if (body == null) {

            Collection<Message.Body> mColl = smackMessage.getBodies();
            for (Message.Body bodyPart : mColl) {
                String msg = bodyPart.getMessage();
                if (msg != null) {
                    body = msg;
                    break;
                }
            }
        }

        DeliveryReceipt drIncoming = smackMessage.getExtension("received", DeliveryReceipt.NAMESPACE);
        if (drIncoming != null) {

            Log.e(TAG, "got delivery receipt for " + drIncoming.getId());
            boolean groupMessage = smackMessage.getType() == Message.Type.groupchat;
                /*
                ChatSession session = findOrCreateSession(address, groupMessage);
                if (session != null)
                    session.onMessageReceipt(drIncoming.getId());
                */
        }

        try
        {
            Constant.debug("body", body);
            InfoTipe object = new Gson().fromJson(body, InfoTipe.class);
            DBHelper database = new DBHelper(mApplicationContext);

            if(object.mitraid.equals(mApplicationContext.getResources().getString(R.string.CONF_MITRAID))) {

                if ((object.id.length() > 2) && (object.id.substring(0, 2).equals(Constant.NOTIF_KODE_BIG_IMAGE))) {
                    showBig(object);
                    database.insertLoghistory(new Loghistory(Constant.NOTIF_KODE_BIG_IMAGE, object));
                }
                else if ((object.id.length() > 2) && (object.id.substring(0, 2).equals(Constant.NOTIF_KODE_HTML_CONTENT)))
                {
                    NotificationCenter.getInstance().updateNotificationHtmlContent(mApplicationContext, object);
                    database.insertLoghistory(new Loghistory(Constant.NOTIF_KODE_HTML_CONTENT, object));
                }
                else if((object.id.length() > 2) && (object.id.substring(0,2).equals(Constant.NOTIF_KODE_CREATE_TOPIK)) ) // broadcast group
                {
                    boolean isOpen = true;
                    Object chat = object.chatdata;
                    if(chat instanceof ChatSingleModel) {

                        ChatSingleModel cht = (ChatSingleModel) chat;
                        boolean seNotif = false;
                        if(!_session.getMemid().equals(Integer.valueOf(cht.mem_id))){
                            if ("create_topik".equals(cht.jenis)) {
                                seNotif = true;
                                Loghistory log = new Loghistory();
                                log.TYPE = Constant.NOTIF_KODE_CREATE_TOPIK;
                                log.TITLE = cht.namapengirim + " membuat topik baru";
                                log.DESCRIPTION = cht.message;
                                log.IDBASE = cht.groupid;
                                log.ACTIVITY = object.activity;
                                log.CREATEDATE = cht.tanggal;
                                database.insertLoghistory(log);
                                database.insertChatGroupByIdBase(new ChatGroupModel(cht));
                            }


                            if ("chat_topik".equals(cht.jenis)) {
                                ChatGroupModel groupModel = database.getSingleChatgorupByIdbase(cht.groupid).get(0);
                                seNotif = (groupModel.JOINGROUP || _session.getMemid().equals(Integer.valueOf(groupModel.CREATORID)));
                                Log.e("sNotif", "notif : " + seNotif);
                                if(seNotif)
                                {
                                    database.insertChatGroupDetail("RosterConnection",false, new ChatGroupDetail(cht, 1));
                                }
                            }

                            if(seNotif) {
                                if(ForegroundDetector.getInstance().isForeground()) EventBus.getDefault().post("badge_diskusi");
                                if (_session.getGroupDetailActivityIsOpen() && (!_session.getGroupIdOpened().equals(""))) {
                                    //kl detail group opened dan pastinya groupid gak kosong
                                    if (_session.getGroupIdOpened().equals(((ChatSingleModel) chat).groupid)) {
                                        // dan ini kalo group id sama di broadcast ke activity detail group
                                        EventBus.getDefault().post(chat);
                                    } else
                                        NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, object);
                                } else if (_session.getGroupActivityIsOpen()) {
                                    EventBus.getDefault().post(chat);
                                } else
                                    NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, object);
                            }
                        }
                    }
                }
                else if((object.id.length() > 2) && (object.id.substring(0,2).equals(Constant.NOTIF_KODE_LIKE)) ) {

                    if(object.likedata instanceof LikestatusModel)
                    {
                        LikestatusModel liker = object.likedata;
                        database.updateOwnlikes(String.valueOf(liker.infoid), liker.totallike);
                        database.updateLastUpdateTimeline(String.valueOf(liker.infoid));
                        EventBus.getDefault().post(liker);
                    }
                }
                else if((object.id.length() > 2) && (object.id.substring(0,2).equals(Constant.NOTIF_KODE_KOMENTAR)) ) {
                    if (!_session.getKomentarActivityIsOpen())
                        NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, object);
                    else // nek kebuka cek infoid ne nek gak sama muncul notif
                    {

                        if(object.detail.size() > 0)
                        {
                            for(int n=0; n < object.detail.size(); n++)
                            {
                                InfoItem detil = object.detail.get(n);

                                if(detil.tipe.toLowerCase().equals("komentar"))
                                {
                                    int total = database.getTotalKomen(String.valueOf(detil.infoid));
                                    database.updateTotalKomenIncreament(String.valueOf(detil.infoid));
                                    database.updateLastUpdateTimeline(String.valueOf(detil.infoid));
                                    if (Integer.valueOf(_session.getLastInfoId()) != detil.infoid)
                                        NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, object);
                                    else
                                        EventBus.getDefault().post(object);
                                }
                                else if(detil.tipe.toLowerCase().equals("timeline"))
                                {

                                }

                            }
                        }

                    }
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("execParse", ex.getMessage());
            //NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, address, body);
        }
    }

    private void showBig(InfoTipe data)
    {
        loadBigImageWithNotif tasck = new loadBigImageWithNotif(mApplicationContext, data);
        tasck.execute();
    }

    class loadBigImageWithNotif extends AsyncTask<String, Integer, Bitmap>
    {
        InfoTipe data;
        Context ctx;
        public loadBigImageWithNotif(Context contxt, InfoTipe obj)
        {
            super();
            ctx = contxt;
            data = obj;
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            try {
                URL url = new URL(data.bgpic);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            NotificationCenter.getInstance().updateNotificationBigImageSimple(mApplicationContext, data, bitmap);
        }
    }
}
