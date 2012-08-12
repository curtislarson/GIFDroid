package com.quackware.gifdroid.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.quackware.gifdroid.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;

public class ErrorReporter implements Thread.UncaughtExceptionHandler
{
 String VersionName;
 String PackageName;
 String FilePath;
 String PhoneModel;
 String AndroidVersion;
 String Board;
 String Brand;
 String Device;
 String Display;
 String FingerPrint;
 String Host; 
 String ID;
 String Manufacturer;
 String Model;
 String Product;
 String Tags;
 long Time;
 String Type;
 String User;
 HashMap<String, String> CustomParameters = new HashMap< String, String>();

 private Thread.UncaughtExceptionHandler PreviousHandler;
 private static ErrorReporter    S_mInstance;
 private Context       CurContext;
 
 public void AddCustomData( String Key, String Value )
 {
  CustomParameters.put( Key, Value );
 }
 
 private String CreateCustomInfoString()
 {
  String CustomInfo = "";
  Iterator<String> iterator = CustomParameters.keySet().iterator();
  while( iterator.hasNext() )
  {
   String CurrentKey = iterator.next();
   String CurrentVal = CustomParameters.get( CurrentKey );
   CustomInfo += CurrentKey + " = " + CurrentVal + "\n";
  }
  return CustomInfo;
 }

 static ErrorReporter getInstance()
 {
  if ( S_mInstance == null )
   S_mInstance = new ErrorReporter();
  return S_mInstance;
 }
 
 public void Init( Context context )
 {
  PreviousHandler = Thread.getDefaultUncaughtExceptionHandler();
  Thread.setDefaultUncaughtExceptionHandler( this );  
  CurContext = context;
 }
 
 public long getAvailableInternalMemorySize() { 
        File path = Environment.getDataDirectory(); 
        StatFs stat = new StatFs(path.getPath()); 
        long blockSize = stat.getBlockSize(); 
        long availableBlocks = stat.getAvailableBlocks(); 
        return availableBlocks * blockSize; 
    } 
     
    public long getTotalInternalMemorySize() { 
        File path = Environment.getDataDirectory(); 
        StatFs stat = new StatFs(path.getPath()); 
        long blockSize = stat.getBlockSize(); 
        long totalBlocks = stat.getBlockCount(); 
        return totalBlocks * blockSize; 
    } 
 
 void RecoltInformations( Context context )
 {
        try
        {
      PackageManager pm = context.getPackageManager();
         PackageInfo pi;
            // Version
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            VersionName = pi.versionName;
            // Package name
            PackageName = pi.packageName;
            // Device model
            PhoneModel = android.os.Build.MODEL;
            // Android version
            AndroidVersion = android.os.Build.VERSION.RELEASE;
           
            Board = android.os.Build.BOARD;
            Brand  = android.os.Build.BRAND;
            Device  = android.os.Build.DEVICE;
            Display = android.os.Build.DISPLAY;
            FingerPrint = android.os.Build.FINGERPRINT;
         Host = android.os.Build.HOST;
         ID = android.os.Build.ID;
         Model = android.os.Build.MODEL;
         Product = android.os.Build.PRODUCT;
         Tags = android.os.Build.TAGS;
         Time = android.os.Build.TIME;
         Type = android.os.Build.TYPE;
         User = android.os.Build.USER;
        }
        catch( Exception e )
        {
         e.printStackTrace();
        }
 }
 
 public String CreateInformationString()
 {
  RecoltInformations( CurContext );
  StringBuilder sb = new StringBuilder();
  sb.append("Version : " + VersionName);
  sb.append("\n");
  sb.append("Package : " + PackageName);
  sb.append("\n");
  sb.append("FilePath : " + FilePath);
  sb.append("\n");
  sb.append("Phone Model: " + PhoneModel);
  sb.append("\n");
  sb.append("Android Version : " + AndroidVersion);
  sb.append("\n");
  sb.append("Board : " + Board);
  sb.append("\n");
  sb.append("Brand : " + Brand);
  sb.append("\n");
  sb.append("Device : " + Device);
  sb.append("\n");
  sb.append("Display : " + Display);
  sb.append("\n");
  sb.append("Finger Print : " + FingerPrint);
  sb.append("\n");
  sb.append("Host : " + Host);
  sb.append("\n");
  sb.append("ID : " + ID);
  sb.append("\n");
  sb.append("Model : " + Model);
  sb.append("\n");
  sb.append("Product : " + Product);
  sb.append("\n");
  sb.append("Tags : " + Tags);
  sb.append("\n");
  sb.append("Time : " + Time);
  sb.append("\n");
  sb.append("Type : " + Type);
  sb.append("\n");
  sb.append("User : " + User);
  sb.append("\n");
  sb.append("Total Internal memory : " + getTotalInternalMemorySize());
  sb.append("\n");
  sb.append("Available Internal memory : " + getAvailableInternalMemorySize());
  sb.append("\n");
  
  return sb.toString();
 }
 
 @Override
public void uncaughtException(Thread t, Throwable e)
 {
  StringBuilder sb = new StringBuilder();
  Date CurDate = new Date();
  sb.append("Error Report collected on : " + CurDate.toString());
  sb.append("\n");
  sb.append("\n");
  sb.append("Informations :");
  sb.append("\n");
  sb.append("==============");
  sb.append("\n");
  sb.append("\n");
  sb.append(CreateInformationString());
  
  sb.append("Custom Informations :\n");
  sb.append("=====================\n");
  sb.append(CreateCustomInfoString());
    
  sb.append("\n\n");
  sb.append("Stack : \n");
  sb.append("======= \n");
  final Writer result = new StringWriter();
  final PrintWriter printWriter = new PrintWriter(result);
  e.printStackTrace(printWriter);
  String stacktrace = result.toString();
  sb.append(stacktrace);

  sb.append("\n");
  sb.append("Cause : \n");
  sb.append("======= \n");
  
  // If the exception was thrown in a background thread inside
  // AsyncTask, then the actual exception can be found with getCause
  Throwable cause = e.getCause();
  while (cause != null)
  {
   cause.printStackTrace( printWriter );
   sb.append(result.toString());
   cause = cause.getCause();
  }
  printWriter.close();
  sb.append("****  End of current Report ***");
  SaveAsFile(sb.toString());
  //SendErrorMail( Report );
  PreviousHandler.uncaughtException(t, e);
 }
 
 private void SendErrorMail( Context _context, String ErrorContent )
 {
  Intent sendIntent = new Intent(Intent.ACTION_SEND);
  String subject = _context.getResources().getString(R.string.crashSubject);
  String body = _context.getResources().getString( R.string.crashBody) +
   "\n\n"+
   ErrorContent+
   "\n\n";
  sendIntent.putExtra(Intent.EXTRA_EMAIL,
    new String[] {"QuackWare@gmail.com"});
  sendIntent.putExtra(Intent.EXTRA_TEXT, body);
  sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
  sendIntent.setType("message/rfc822");
  _context.startActivity( Intent.createChooser(sendIntent, "Please select a mail option to send a crash report.") );
 }
 private void SaveAsFile( String ErrorContent )
 {
  try
  {
   Random generator = new Random();
   int random = generator.nextInt(99999);
   String FileName = "stack-" + random + ".stacktrace";
   FileOutputStream trace = CurContext.openFileOutput( FileName, Context.MODE_PRIVATE);
   trace.write(ErrorContent.getBytes());
   trace.close();
  }
  catch( Exception e )
  {
   // ...
  }
 }
 
 private String[] GetErrorFileList()
 {
  File dir = new File( FilePath + "/");
        // Try to create the files folder if it doesn't exist
        dir.mkdir();
        // Filter for ".stacktrace" files
        FilenameFilter filter = new FilenameFilter() {
                @Override
				public boolean accept(File dir, String name) {
                        return name.endsWith(".stacktrace");
                }
        };
        return dir.list(filter);
 }
 private boolean bIsThereAnyErrorFile()
 {
  return GetErrorFileList().length > 0;
 }
 public void CheckErrorAndSendMail(Context _context )
 {
  try
  {
   FilePath = _context.getFilesDir().getAbsolutePath();
   if ( bIsThereAnyErrorFile() )
   {
    String WholeErrorText = "";
     // on limite � N le nombre d'envois de rapports ( car trop lent )
    String[] ErrorFileList = GetErrorFileList();
    int curIndex = 0;
    final int MaxSendMail = 5;
    for ( String curString : ErrorFileList )
    {
     if ( curIndex++ <= MaxSendMail )
     {
      WholeErrorText +="Thanks for submitting a report. If you can, please list a few steps to reproduce the problem so I can fix it easier! All the other information will be automatically generated \n \n";
      
      WholeErrorText+="New Trace collected :\n";
      WholeErrorText+="=====================\n ";
      String filePath = FilePath + "/" + curString;
      BufferedReader input =  new BufferedReader(new FileReader(filePath));
      String line;

      while (( line = input.readLine()) != null)
      {
       WholeErrorText += line + "\n";
      }
      input.close();
     }

     // DELETE FILES !!!!
     File curFile = new File( FilePath + "/" + curString );
     curFile.delete();
    }
    SendErrorMail( _context , WholeErrorText );
   }
  }
  catch( Exception e )
  {
   e.printStackTrace();
  }
 }
}