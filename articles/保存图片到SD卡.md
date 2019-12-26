```
 /**
     * 保存到本地相册
     * @param context
     * @param bmp
     */
    public void saveImageToGallery(Context context, Bitmap bmp) {
        Log.d("ZoomImage", "saveImageToGallery:" + bmp);
        final String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)
                ? Environment.getExternalStorageDirectory().getAbsolutePath()
                : "/mnt/sdcard";//保存到SD卡

        // 首先保存图片
        File appDir = new File(SAVE_PIC_PATH + "/ZoomImage/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        long nowSystemTime = System.currentTimeMillis();
        String fileName = nowSystemTime + ".png";
        File file = new File(appDir, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //保存图片后发送广播通知更新数据库
        Uri uri = Uri.fromFile(file);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));


//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        // 最后通知图库更新
//        context.sendBroadcast(
//                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        Toast.makeText(getContext(), "已保存到本地相册", Toast.LENGTH_LONG).show();
    }
```
