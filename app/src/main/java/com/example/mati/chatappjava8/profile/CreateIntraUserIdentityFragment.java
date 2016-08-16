package com.example.mati.chatappjava8.profile;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.exceptions.ActorAlreadyRegisteredException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.exceptions.CantRegisterActorException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.example.mati.app_core.Core;
import com.example.mati.chatappjava8.R;
import com.example.mati.chatappjava8.list.ListActivity;

import org.iop.ns.chat.ChatNetworkServicePluginRoot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Updated by Andres Abreu aabreu1 27/06/16
 */
public class CreateIntraUserIdentityFragment extends Fragment {


    private static final String TAG = "CreateIdentity";

    private static final int CREATE_IDENTITY_FAIL_MODULE_IS_NULL = 0;
    private static final int CREATE_IDENTITY_FAIL_NO_VALID_DATA = 1;
    private static final int CREATE_IDENTITY_FAIL_MODULE_EXCEPTION = 2;
    private static final int CREATE_IDENTITY_SUCCESS = 3;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;

    private static final int CONTEXT_MENU_CAMERA = 1;
    private static final int CONTEXT_MENU_GALLERY = 2;
    private byte[] brokerImageByteArray;
    private Button createButton;
    private EditText mBrokerName;
    private ImageView mBrokerImage;
    private ImageView mphoto_header;
    private RelativeLayout relativeLayout;
    private Menu menuHelp;
    Toolbar toolbar;
//    private IntraUserModuleIdentity identitySelected;
    private boolean isUpdate = false;
    private EditText mBrokerPhrase;
//    IntraUserIdentitySettings intraUserIdentitySettings = null;
    private boolean updateProfileImage = false;
    private boolean contextMenuInUse = false;
//    private IntraUserIdentityModuleManager moduleManager;
    private Uri imageToUploadUri;
    private Bitmap imageBitmap;
//    private Location locationManager;
    //private Bitmap imageBitmap = null;

    private ImageView mChatImage;
    ExecutorService executorService;

    private ChatNetworkServicePluginRoot manager;

    public static CreateIntraUserIdentityFragment newInstance(ChatNetworkServicePluginRoot chatNetworkServicePluginRoot) {
        CreateIntraUserIdentityFragment createIntraUserIdentityFragment = new CreateIntraUserIdentityFragment();
        createIntraUserIdentityFragment.setManager(chatNetworkServicePluginRoot);
        return createIntraUserIdentityFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        executorService = Executors.newFixedThreadPool(3);
        try {


//            if(moduleManager.getAllIntraWalletUsersFromCurrentDeviceUser().isEmpty()){
//                moduleManager.createNewIntraWalletUser("John Doe", null);
//            }

        } catch (Exception ex) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootLayout = inflater.inflate(R.layout.fragment_main, container, false);
        initViews(rootLayout);
        setUpIdentity();
        SharedPreferences pref = getActivity().getSharedPreferences("dont show dialog more", Context.MODE_PRIVATE);
//        if (!pref.getBoolean("isChecked", false)) {
//            PresentationIntraUserIdentityDialog presentationIntraUserCommunityDialog = new PresentationIntraUserIdentityDialog(getActivity(), null, null);
//            presentationIntraUserCommunityDialog.show();
//        }



        return rootLayout;
    }


    /**
     * Inicializa las vistas de este Fragment
     *
     * @param layout el layout de este Fragment que contiene las vistas
     */
    private void initViews(View layout) {
        createButton = (Button) layout.findViewById(R.id.create_crypto_broker_button);
        mBrokerName = (EditText) layout.findViewById(R.id.crypto_broker_name);
        mBrokerPhrase = (EditText) layout.findViewById(R.id.crypto_broker_phrase);
        mBrokerImage = (ImageView) layout.findViewById(R.id.img_photo);
        relativeLayout = (RelativeLayout) layout.findViewById(R.id.user_image);
        mphoto_header = (ImageView) layout.findViewById(R.id.img_photo_header);



        //createButton.setText((!isUpdate) ? "Create" : "Update");

        mBrokerName.requestFocus();
        registerForContextMenu(mBrokerImage);

        mBrokerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CommonLogger.debug(TAG, "Entrando en mBrokerImage.setOnClickListener");
                getActivity().openContextMenu(mBrokerImage);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CommonLogger.debug(TAG, "Entrando en createButton.setOnClickListener");


                if (CREATE_IDENTITY_SUCCESS==createNewIdentity()){
                    Intent intent = new Intent(getActivity(),ListActivity.class);
                    startActivity(intent);
                }


            }
        });
    }

    private void publishResult(final int resultKey){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (resultKey) {
                    case CREATE_IDENTITY_SUCCESS:
//                        changeActivity(Activities.CCP_SUB_APP_INTRA_USER_IDENTITY.getCode(), appSession.getAppPublicKey());
                        if (!isUpdate) {
                            Toast.makeText(getActivity(), "Identity created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_SHORT).show();
                        }
                        getActivity().onBackPressed();
                        break;
                    case CREATE_IDENTITY_FAIL_MODULE_EXCEPTION:
                        Toast.makeText(getActivity(), "Error al crear la identidad", Toast.LENGTH_LONG).show();
                        break;
                    case CREATE_IDENTITY_FAIL_NO_VALID_DATA:
                        Toast.makeText(getActivity(), "La data no es valida", Toast.LENGTH_LONG).show();
                        break;
                    case CREATE_IDENTITY_FAIL_MODULE_IS_NULL:
                        Toast.makeText(getActivity(), "No se pudo acceder al module manager, es null", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void setUpIdentity() {
        try {

//            identitySelected = (IntraUserModuleIdentity) appSession.getData(SessionConstants.IDENTITY_SELECTED);
//
//
//            if (identitySelected != null) {
//                loadIdentity();
//            } else {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ActiveActorIdentityInformation activeActorIdentityInformation = null;
//                        try {
//                            activeActorIdentityInformation = appSession.getModuleManager().getSelectedActorIdentity();
//                        } catch (CantGetSelectedActorIdentityException e) {
//                            e.printStackTrace();
//                        } catch (ActorIdentityNotSelectedException e) {
//                            e.printStackTrace();
//                        }
//                        if(activeActorIdentityInformation!=null){
//                            identitySelected = (IntraUserModuleIdentity) activeActorIdentityInformation;
//                        }
//                        getActivity().runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            if (identitySelected != null) {
//                                                                loadIdentity();
//                                                                isUpdate = true;
//                                                                createButton.setText("Save changes");
//                                                                createButton.setBackgroundColor(Color.parseColor("#7DD5CA"));
//                                                            }
//                                                        }
//                                                    }
//                        );
//
//                    }
//                }).start();
//
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadIdentity(){
//        if (identitySelected.getImage() != null) {
//            Bitmap bitmap = null;
//            if (identitySelected.getImage().length > 0) {
//                bitmap = BitmapFactory.decodeByteArray(identitySelected.getImage(), 0, identitySelected.getImage().length);
//                BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(mphoto_header,getResources(),false);
//                bitmapWorkerTask.execute(identitySelected.getImage());
//                mphoto_header.setAlpha(150);
////                bitmap = Bitmap.createScaledBitmap(bitmap, mBrokerImage.getWidth(), mBrokerImage.getHeight(), true);
//            } else {
//                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_male);
//
//                Picasso.with(getActivity()).load(R.drawable.ic_profile_male).into(mphoto_header);
//            }
//            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
//            brokerImageByteArray = toByteArray(bitmap);
//            mBrokerImage.setImageDrawable(ImagesUtils.getRoundedBitmap(getResources(), bitmap));
//
//        }
//        mBrokerName.setText(identitySelected.getAlias());
//        mBrokerPhrase.setText(identitySelected.getPhrase());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            contextMenuInUse = true;
            ImageView pictureView = mBrokerImage;

            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    if (imageToUploadUri != null) {
                        String provider = "com.android.providers.media.MediaProvider";
                        Uri selectedImage = imageToUploadUri;
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                getActivity().getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                getActivity().grantUriPermission(provider, selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                getActivity().grantUriPermission(provider, selectedImage, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                getActivity().grantUriPermission(provider, selectedImage, Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                                getActivity().requestPermissions(
                                        new String[]{Manifest.permission.CAMERA},
                                        REQUEST_IMAGE_CAPTURE);
                            }
                        }
                        getActivity().getContentResolver().notifyChange(selectedImage, null);
                        Bitmap reducedSizeBitmap = getBitmap(imageToUploadUri.getPath());
                        if (reducedSizeBitmap != null) {
                            imageBitmap = reducedSizeBitmap;
                        }
                    }
                    try {
                        if (checkCameraPermission()) {
                            if (checkWriteExternalPermission()) {
                                if (imageBitmap != null) {
                                  //  if (imageBitmap.getWidth() >= 192 && imageBitmap.getHeight() >= 192) {
//                                        final DialogCropImage dialogCropImage = new DialogCropImage(getActivity(), appSession, null, imageBitmap);
//                                        dialogCropImage.show();
//                                        dialogCropImage.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                            @Override
//                                            public void onDismiss(DialogInterface dialog) {
//                                                if (dialogCropImage.getCroppedImage() != null) {
//                                                    imageBitmap = getResizedBitmap(rotateBitmap(dialogCropImage.getCroppedImage(), ExifInterface.ORIENTATION_NORMAL), dpToPx(), dpToPx());
//                                                    mBrokerImage.setImageDrawable(ImagesUtils.getRoundedBitmap(getResources(), imageBitmap));
//                                                    brokerImageByteArray = toByteArray(imageBitmap);
//                                                    updateProfileImage = true;
//
//                                                    BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(mphoto_header,getResources(),false);
//                                                    bitmapWorkerTask.execute(brokerImageByteArray );
//                                                    mphoto_header.setAlpha(150);
//
//                                                } else {
//                                                    imageBitmap = null;
//                                                }
//                                            }
//                                        });
                                   // } else {
                                  //      Toast.makeText(getActivity(), "The image selected is too small. Please select a photo with height and width of at least 192x192", Toast.LENGTH_LONG).show();
                                        // cryptoBrokerBitmap = null;
                                        //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                                   // }
                                } else {
                                    Toast.makeText(getActivity(), "Error on upload image", Toast.LENGTH_LONG).show();
                                    //  cryptoBrokerBitmap = null;
                                    //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG).show();
                                // cryptoBrokerBitmap = null;
                                //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG).show();
                            //  cryptoBrokerBitmap = null;
                            //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
//                        errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
                    }

                    break;
                case REQUEST_LOAD_IMAGE:
                 //   Uri selectedImage = data.getData();
                 //   try {
                 //       if (isAttached) {
                 //           ContentResolver contentResolver = getActivity().getContentResolver();
                 //           imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage);
                 //           imageBitmap = Bitmap.createScaledBitmap(imageBitmap, mBrokerImage.getWidth(), mBrokerImage.getHeight(), true);
                 //           brokerImageByteArray = toByteArray(imageBitmap);
                 //           updateProfileImage = true;
                 //           Picasso.with(getActivity()).load(selectedImage).transform(new CircleTransform()).into(mBrokerImage);
                 //       }
                 //   } catch (Exception e) {
                 //       e.printStackTrace();
                 //       Toast.makeText(getActivity().getApplicationContext(), "Error cargando la imagen", Toast.LENGTH_SHORT).show();
                 //   }

                    Uri selectedImage = data.getData();
                    try {
//                        if (isAttached) {
//                            ContentResolver contentResolver = getActivity().getContentResolver();
//                            imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage);
//                            //cryptoBrokerBitmap = Bitmap.createScaledBitmap(cryptoBrokerBitmap, mBrokerImage.getWidth(), mBrokerImage.getHeight(), true);
//                            if (imageBitmap.getWidth() >= 192 && imageBitmap.getHeight() >= 192) {
//                                // cryptoBrokerBitmap = ImagesUtils.cropImage(cryptoBrokerBitmap);
//                                final DialogCropImage dialogCropImagee = new DialogCropImage(getActivity(), appSession, null, imageBitmap);
//                                dialogCropImagee.show();
//                                dialogCropImagee.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                    @Override
//                                    public void onDismiss(DialogInterface dialog) {
//                                        if (dialogCropImagee.getCroppedImage() != null) {
//                                            imageBitmap = getResizedBitmap(rotateBitmap(dialogCropImagee.getCroppedImage(), ExifInterface.ORIENTATION_NORMAL), dpToPx(), dpToPx());
//                                            mBrokerImage.setImageDrawable(ImagesUtils.getRoundedBitmap(getResources(), imageBitmap));
//                                            brokerImageByteArray = toByteArray(imageBitmap);
//
//                                            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(mphoto_header,getResources(),false);
//                                            bitmapWorkerTask.execute(brokerImageByteArray);
//                                            mphoto_header.setAlpha(150);
//                                            updateProfileImage = true;
//                                        } else {
//                                            imageBitmap = null;
//                                        }
//                                    }
//                                });
//                            } else {
//                                Toast.makeText(getActivity(), "The image selected is too small. Please select a photo with height and width of at least 192x192", Toast.LENGTH_LONG).show();
//                                // cryptoBrokerBitmap = null;
//                                // Toast.makeText(getActivity(), "The image selected is too small", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
                    } catch (Exception e) {
                       // errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
                        Toast.makeText(getActivity().getApplicationContext(), "Error loading the image", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }

//            final Bitmap finalImageBitmap = imageBitmap;
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mBrokerImage != null && finalImageBitmap != null) {
////                        mBrokerImage.setImageDrawable(new BitmapDrawable(getResources(), finalImageBitmap));
//                        mBrokerImage.setImageDrawable(ImagesUtils.getRoundedBitmap(getResources(), finalImageBitmap));
//                    }
//                }
//            });

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Choose mode");
//        menu.setHeaderIcon(getActivity().getResources().getDrawable(R.drawable.ic_camera_green));
        menu.add(Menu.NONE, CONTEXT_MENU_CAMERA, Menu.NONE, "Camera");
        menu.add(Menu.NONE, CONTEXT_MENU_GALLERY, Menu.NONE, "Gallery");

        super.onCreateContextMenu(menu, view, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(!contextMenuInUse) {
            switch (item.getItemId()) {
                case CONTEXT_MENU_CAMERA:
                    dispatchTakePictureIntent();
                    contextMenuInUse = true;
                    return true;
                case CONTEXT_MENU_GALLERY:
                    loadImageFromGallery();
                    contextMenuInUse = true;
                    return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Crea una nueva identidad para un crypto broker
     *
     * @return key con el resultado de la operacion:<br/><br/>
     * <code>CREATE_IDENTITY_SUCCESS</code>: Se creo exitosamente una identidad <br/>
     * <code>CREATE_IDENTITY_FAIL_MODULE_EXCEPTION</code>: Se genero una excepcion cuando se ejecuto el metodo para crear la identidad en el Module Manager <br/>
     * <code>CREATE_IDENTITY_FAIL_MODULE_IS_NULL</code>: No se tiene una referencia al Module Manager <br/>
     * <code>CREATE_IDENTITY_FAIL_NO_VALID_DATA</code>: Los datos ingresados para crear la identidad no son validos (faltan datos, no tiene el formato correcto, etc) <br/>
     */
    private int createNewIdentity() {

        final String brokerNameText = mBrokerName.getText().toString();
        String brokerPhraseText = "";

        if (!mBrokerPhrase.getText().toString().isEmpty()){
             brokerPhraseText = mBrokerPhrase.getText().toString();
        }else{
            brokerPhraseText = "Available";
        }

        boolean dataIsValid = validateIdentityData(brokerNameText, brokerPhraseText, brokerImageByteArray);

        if (dataIsValid) {
            final ActorProfile profile = new ActorProfile();
            profile.setIdentityPublicKey(UUID.randomUUID().toString());
            System.out.println("I will try to register an actor with pk " + profile.getIdentityPublicKey());
            profile.setActorType(Actors.CHAT.getCode());
            profile.setName(mBrokerName.getText().toString());
            profile.setAlias("Alias chat");
            //This represents a valid image
            profile.setPhoto(brokerImageByteArray);
            profile.setNsIdentityPublicKey(manager.getNetWorkServicePublicKey());
            profile.setExtraData("Test extra data");
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Core.getInstance().setProfile(profile);
                        manager.registerActor(profile, 0, 0);
                    } catch (ActorAlreadyRegisteredException e) {
                        e.printStackTrace();
                    } catch (CantRegisterActorException e) {
                        e.printStackTrace();
                    }
                }
            });

            return CREATE_IDENTITY_SUCCESS;
            //            if (moduleManager != null) {
//                try {
//                    if (!isUpdate) {
//                        final String finalBrokerPhraseText = brokerPhraseText;
//                        executorService.submit(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//
//                                    moduleManager.createNewIntraWalletUser(brokerNameText, finalBrokerPhraseText, (brokerImageByteArray == null) ? convertImage(R.drawable.ic_profile_male) : brokerImageByteArray, (long)100, Frequency.NORMAL, moduleManager.getLocationManager());
//
//                                    publishResult(CREATE_IDENTITY_SUCCESS);
//                                } catch (CantCreateNewIntraUserIdentityException e) {
//                                    e.printStackTrace();
//                                }
//                                catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                    else {
//                        final String finalBrokerPhraseText1 = brokerPhraseText;
//                        executorService.submit(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
////                                    if (!exist){
////                                        moduleManager.createIdentity("Mati","aaa",null);
////                                        exist = true;
////                                    }
//                                    if (updateProfileImage)
//
//                                        moduleManager.updateIntraUserIdentity(identitySelected.getPublicKey(), brokerNameText, finalBrokerPhraseText1, brokerImageByteArray, (long)100, Frequency.NORMAL, moduleManager.getLocationManager());
//
//                                    else
//                                        moduleManager.updateIntraUserIdentity(identitySelected.getPublicKey(), brokerNameText, finalBrokerPhraseText1, identitySelected.getImage(), (long)100, Frequency.NORMAL,moduleManager.getLocationManager());
//                                    publishResult(CREATE_IDENTITY_SUCCESS);
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                        //todo: sacar
////                        return CREATE_IDENTITY_FAIL_NO_VALID_DATA;
//                    }
//
//                 } catch (Exception e) {
//                    errorManager.reportUnexpectedUIException(UISource.VIEW, UnexpectedUIExceptionSeverity.UNSTABLE, e);
//
//                }
//                return CREATE_IDENTITY_SUCCESS;
//            }
//            return CREATE_IDENTITY_FAIL_MODULE_IS_NULL;
        }
        return CREATE_IDENTITY_FAIL_NO_VALID_DATA;

    }

    boolean exist = false;

    private byte[] convertImage(int resImage){
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), resImage);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream);
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void dispatchTakePictureIntent() {
        Log.i(TAG, "Opening Camera app to take the picture...");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void loadImageFromGallery() {
        Log.i(TAG, "Loading Image from Gallery...");

        Intent loadImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(loadImageIntent, REQUEST_LOAD_IMAGE);
    }

    private boolean validateIdentityData(String brokerNameText, String brokerPhraseText, byte[] brokerImageBytes) {
        if (brokerNameText.isEmpty())
            return false;
        if (brokerPhraseText.isEmpty())
            return false;
        if (brokerImageBytes == null)
            return true;
        if (brokerImageBytes.length > 0)
            return true;

        return true;
    }

    /**
     * Bitmap to byte[]
     *
     * @param bitmap Bitmap
     * @return byte array
     */
    private byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }


    public void showDialog(){
        if(getActivity()!=null) {
//            PresentationIntraUserIdentityDialog presentationIntraUserCommunityDialog = new PresentationIntraUserIdentityDialog(getActivity(), appSession, null, appSession.getModuleManager());
//            presentationIntraUserCommunityDialog.show();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_main, menu);

      /*  try {
            menu.add(1, 99, 1, "help").setIcon(R.drawable.help_icon)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);



            final MenuItem action_help = menu.findItem(R.id.action_help);
            menu.findItem(R.id.action_help).setVisible(true);
            action_help.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    menu.findItem(R.id.action_help).setVisible(false);
                    return false;
                }
            });

        } catch (Exception e) {

        }*/

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        try {
//            int id = item.getItemId();
//
//            if (id == 1)
//                showDialog();
//
//            if (id == 2)
//                if(identitySelected!=null)
//                    changeActivity(Activities.CCP_SUB_APP_INTRA_IDENTITY_GEOLOCATION_IDENTITY, appSession.getAppPublicKey());
//                else
//                    showDialog();
//
//
//
//        } catch (Exception e) {
//            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
//            makeText(getActivity(), "Oooops! recovering from system error",
//                    LENGTH_LONG).show();
//        }
        return super.onOptionsItemSelected(item);
    }


    private Bitmap getBitmap(String path) {
        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 3000000; // 1.2MP
            in = getActivity().getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getActivity().getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    private boolean checkCameraPermission() {
        String permission = "android.permission.CAMERA";
        int res = getActivity().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkWriteExternalPermission() {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = getActivity().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    public int dpToPx() {
        int dp = 150;
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    public void setManager(ChatNetworkServicePluginRoot manager) {
        this.manager = manager;
    }
}
