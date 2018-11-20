package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.models.DecodeImageBean;
import com.cfcs.komaxcustomer.utils.SimpleSpanBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppFeedBack";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppFeedBack";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";

    Button btn_submit_feedback, btn_image_choose;
    EditText txt_subject, txt_refrence, txt_message;
    TextView all_images;
    ImageView image_view;


    String subject = "", message = "", referrence = "", imageJson = "";
    int currentapiVersion = 0;

    ArrayList<String> view_images;

    Uri mCapturedImageURI;

    private Bitmap imgbitmap;
    private PopupWindow pwindo;

    ScrollView scroll_feedback;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    LinearLayout maincontainer;

    TextView tv_subject, tv_refrence, tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //Set Company logo in action bar with AppCompatActivity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        tv_subject = findViewById(R.id.tv_subject);
        tv_refrence = findViewById(R.id.tv_refrence);
        tv_message = findViewById(R.id.tv_message);

        SimpleSpanBuilder ssbSubject = new SimpleSpanBuilder();
        ssbSubject.appendWithSpace("Subject");
        ssbSubject.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_subject.setText(ssbSubject.build());

        SimpleSpanBuilder ssbRefernce = new SimpleSpanBuilder();
        ssbRefernce.appendWithSpace("Reference");
        ssbRefernce.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_refrence.setText(ssbRefernce.build());

        SimpleSpanBuilder ssbMessage = new SimpleSpanBuilder();
        ssbMessage.appendWithSpace("Message");
        ssbMessage.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_message.setText(ssbMessage.build());

        txt_subject = findViewById(R.id.txt_subject);
        txt_message = findViewById(R.id.txt_message);
        txt_refrence = findViewById(R.id.txt_refrence);
        all_images = findViewById(R.id.all_images);
        btn_image_choose = findViewById(R.id.btn_image_choose);
        btn_submit_feedback = findViewById(R.id.btn_submit_feedback);
        image_view = findViewById(R.id.image_view);
        scroll_feedback = findViewById(R.id.scroll_feedback);
        maincontainer = findViewById(R.id.maincontainer);

        view_images = new ArrayList<String>();
        currentapiVersion = android.os.Build.VERSION.SDK_INT;
        btn_image_choose.setOnClickListener(this);
        image_view.setOnClickListener(this);
        btn_submit_feedback.setOnClickListener(this);
        image_view.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_submit_feedback:
                subject = txt_subject.getText().toString().trim();
                referrence = txt_refrence.getText().toString().trim();
                message = txt_message.getText().toString().trim();
                if ((subject.compareTo("") != 0) && (message.compareTo("") != 0) & (referrence.compareTo("") != 0)) {
                    btn_submit_feedback.setClickable(false);
                    Config_Customer.isOnline(FeedbackActivity.this);
                    if (Config_Customer.internetStatus == true) {

                        new FeedbackcAsync().execute();

                    } else {
                        Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", FeedbackActivity.this);
                    }

                } else {
                    if (TextUtils.isEmpty(subject)) {
                        Config_Customer.alertBox("Please Enter Subject", FeedbackActivity.this);
                        txt_subject.requestFocus();
                        focusOnView();

                    } else if (TextUtils.isEmpty(referrence)) {

                        Config_Customer.alertBox("Please Enter Reference", FeedbackActivity.this);
                        txt_refrence.requestFocus();
                    } else if (TextUtils.isEmpty(message)) {
                        Config_Customer.alertBox("Please Enter Message", FeedbackActivity.this);
                        txt_message.requestFocus();
                    }
                }
                break;

            case R.id.image_view:

                if (view_images.size() > 0) {
                    initiateImagePopupWindow();
                } else {
                    Config_Customer.toastShow("No images selected", this);
                }
                break;

            case R.id.btn_image_choose:

                if (view_images.size() < 4) {

                    if (currentapiVersion <= 22) {

                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "Image File name");
                        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intentImg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intentImg.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                        startActivityForResult(intentImg, 0);

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                            } else if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            } else {
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, "Image File name");
                                mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                Intent intentImg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intentImg.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                                startActivityForResult(intentImg, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                            }
                        }
                    }

                } else {

                    Config_Customer.toastShow("You can not attach more then 4 images !!", FeedbackActivity.this);
                }
                break;
            default:
                break;
        }
    }

    private void focusOnView() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                scroll_feedback.smoothScrollTo(0, 0);
            }
        });
    }

    private void initiateImagePopupWindow() {

        Button btnOK;
        TextView tvEmptyView;
        ListView ImgList;
        RecyclerView recycler_view;

        try {
            LayoutInflater inflater = (LayoutInflater) FeedbackActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.layout_view_image,
                    (ViewGroup) findViewById(R.id.popupLayout));
            pwindo = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            btnOK = (Button) layout.findViewById(R.id.btnOK);
            //     tvEmptyView = (TextView) layout.findViewById(R.id.tvEmptyView);
            //   ImgList = (ListView) layout.findViewById(R.id.ImgList);

            //tvEmptyView.setVisibility(View.VISIBLE);
//            ImgList.setEmptyView(tvEmptyView);
//            ImgList.setAdapter(new SelectedImageAdapter(this));
            recycler_view = layout.findViewById(R.id.recycler_view);


            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(new SelectedImageAdapter(this));

            layout.invalidate();

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    if (view_images.size() > 0) {
                        all_images.setText(view_images.size() + " Image");
                    } else {
                        all_images.setText("No Image");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String selectedImagePath = getRealPathFromURI(mCapturedImageURI);
                view_images.add(selectedImagePath);

                if (view_images.size() > 0) {
                    all_images.setText(view_images.size() + " Image");
                    image_view.setVisibility(View.VISIBLE);
                } else {
                    all_images.setText("No Image");
                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    public class FeedbackcAsync extends AsyncTask<String, String, String> {
        int flag;
        String jsonValue;

        String msgstatus;

        String LoginStatus;
        String invalid = "LoginFailed";


        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(FeedbackActivity.this, "Loading", "Please wait...", true, false);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            if (view_images.size() > 0) {
                makeJson();
            }

            String ContactPersonId = Config_Customer.getSharedPreferences(FeedbackActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(FeedbackActivity.this, "pref_Customer", "AuthCode", "");

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("Subject", subject);
            request.addProperty("Message", message);
            request.addProperty("AuthCode", AuthCode);
            request.addProperty("RefNo", referrence);
            request.addProperty("ImgJson", imageJson);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    jsonValue = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(jsonValue);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("status")) {
                        LoginStatus = jsonObject.getString("status");
                        msgstatus = jsonObject.getString("MsgNotification");
                        if (LoginStatus.equals(invalid)) {

                            flag = 4;
                        } else {

                            flag = 1;
                        }
                    } else {
                        flag = 2;
                    }
                } else {
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 5;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("jsonValue", "cfcs" + jsonValue);
            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, FeedbackActivity.this);
                Intent intent = new Intent(FeedbackActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (flag == 2) {
                    Config_Customer.toastShow(msgstatus, FeedbackActivity.this);

                } else if (flag == 3) {
                    Config_Customer.toastShow("No Response", FeedbackActivity.this);
                } else {
                    if (flag == 4) {

                        Config_Customer.toastShow(msgstatus, FeedbackActivity.this);
                        Intent i = new Intent(FeedbackActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else if (flag == 5) {
                        ScanckBar();
                        btn_submit_feedback.setEnabled(false);

                    }
                }
            }
            progressDialog.dismiss();
            btn_submit_feedback.setClickable(true);
        }


        private void ScanckBar() {

            Snackbar snackbar = Snackbar
                    .make(maincontainer, "Connectivity issues", Snackbar.LENGTH_LONG)
                    .setDuration(60000)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            btn_submit_feedback.setEnabled(true);
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            snackbar.show();

        }

        public void makeJson() {

            try {
                Gson gson = new Gson();
                JSONObject jsonObj = new JSONObject();
                JSONArray array = new JSONArray();
                for (int i = 0; i < view_images.size(); i++) {
                    String imgPath = view_images.get(i);
                    final DecodeImageBean diary = getImageObjectFilled(imgPath);
                    String case_json = gson.toJson(diary);
                    JSONObject objCase = new JSONObject(case_json);
                    array.put(objCase);
                    //jsonObj.put("ImagesJson ", array);
                    jsonObj.put("members", array);
                }
                Log.e("ImagesJson", " cfcs " + jsonObj.toString());
                imageJson = jsonObj.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private DecodeImageBean getImageObjectFilled(String imgPath) {

            DecodeImageBean bean = new DecodeImageBean();

            //String MimeType = getMimeType(imgPath);
            //String ImgExtension = MimeTypeMap.getFileExtensionFromUrl(imgPath).toLowerCase();
            int dotposition = imgPath.lastIndexOf(".");
            String ImgExtension = imgPath.substring(dotposition + 1, imgPath.length());
            //Log.e("ImgExtension", "cfcs " +ImgExtension);
            String ImgString = decodeImage(imgPath);
            //String ImgString = imgPath;
            //bean.setMIMEType(MimeType);
            bean.setImageExtension(ImgExtension);
            bean.setImageString(ImgString);
            return bean;
        }

        public String decodeImage(String imgPath) {

            Bitmap decodedBitmap = BitmapFactory.decodeFile(imgPath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] ba = baos.toByteArray();
            String imgString = Base64.encodeToString(ba, Base64.DEFAULT);
            return imgString;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FeedbackActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.dashboard:
                Intent intent;
                intent = new Intent(FeedbackActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(FeedbackActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(FeedbackActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }

                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(FeedbackActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(FeedbackActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(FeedbackActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(FeedbackActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(FeedbackActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(FeedbackActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(FeedbackActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(FeedbackActivity.this);
                finish();
                Config_Customer.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

            case R.id.download_file:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://app.komaxindia.co.in/Customer/Customer-User-Manual.pdf"));
                startActivity(browserIntent);
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }

//    class SelectedImageAdapter extends BaseAdapter {
//
//        Context context;
//        LayoutInflater inflater;
//
//        public SelectedImageAdapter(Context context) {
//            this.context = context;
//            inflater = LayoutInflater.from(this.context);
//        }
//
//        @Override
//        public int getCount() {
//            return view_images.size();
//        }
//
//        @Override
//        public String getItem(int position) {
//            return view_images.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            MyViewHolder mViewHolder;
//            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.layout_image_view, parent, false);
//                mViewHolder = new MyViewHolder(convertView);
//                convertView.setTag(mViewHolder);
//            } else {
//                mViewHolder = (MyViewHolder) convertView.getTag();
//            }
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 8;
//
//            imgbitmap = BitmapFactory.decodeFile(view_images.get(position), options);
//
//            mViewHolder.selected_imageView.setImageBitmap(imgbitmap);
//
//            mViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    view_images.remove(position);
//                    notifyDataSetChanged();
//                }
//            });
//            return convertView;
//        }
//
//        class MyViewHolder {
//
//            ImageView selected_imageView;
//            ImageButton btnDelete;
//
//            public MyViewHolder(View item) {
//                selected_imageView = (ImageView) item.findViewById(R.id.selected_imageView);
//                btnDelete = (ImageButton) item.findViewById(R.id.btnDelete);
//            }
//        }
//    }


    class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.MyViewHolder> {

        Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView selected_imageView;
            ImageButton btnDelete;

            public MyViewHolder(View view) {
                super(view);
                selected_imageView = (ImageView) view.findViewById(R.id.selected_imageView);
                btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
            }
        }


        public SelectedImageAdapter(Context context) {
            this.context = context;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_image_view, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            imgbitmap = BitmapFactory.decodeFile(view_images.get(position), options);

            holder.selected_imageView.setImageBitmap(imgbitmap);

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view_images.remove(position);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return view_images.size();
        }
    }

}
