package name.tanglei.airplaneswitcher.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TangLei on 15/1/5.
 */
public class OperationLogUtils
{
    private static String logFilename = "name.tanglei.airplanemodeswitcher.log";
    private static SimpleDateFormat logTimeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private OperationLogUtils()
    {
    }

    private static String logPrefix()
    {
        return logTimeFmt.format(new Date()) + "\t";
    }
    public static boolean addOperation(String content)
    {
        return addOperation(true, content, true);
    }

    public static boolean addOperation(boolean time, String content, boolean newline)
    {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                File file = new File(Environment.getExternalStorageDirectory(),logFilename);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
                if(time)
                    writer.write(logPrefix());
                writer.write(content);
                if(newline)
                    writer.newLine();
                writer.close();;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String getOperationLogs()
    {
        StringBuffer buffer = new StringBuffer();
        try {
            File file = new File(Environment.getExternalStorageDirectory(),logFilename);
            InputStreamReader in = new InputStreamReader(new FileInputStream(file), "utf-8");
            BufferedReader io = new BufferedReader(in);
            String line = null;
            while((line = io.readLine()) != null)
                buffer.append(line + "\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static boolean delLog()
    {
        try
        {
            RandomAccessFile file = new RandomAccessFile(new File(
                    Environment.getExternalStorageDirectory(),logFilename), "rw");
            file.getChannel().truncate(0);
            return true;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
