package com.technoindians.library;

import android.webkit.MimeTypeMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 06/07/2016
 * Last modified 15/07/2016
 *
 */

public class CheckFileType {

	public static int videoFile(String filepath){
		int i=2;
		//3gp, .mp4, .flv, .wmv, .webm, .avi, .mov
		Set<String> words = new HashSet<String>();
	     words.add("mp4");
	     words.add("3gp");
	     words.add("flv");
	     words.add("wmv");	    
	     words.add("webm");	
	     words.add("avi");
	     words.add("mov");
		 for (String word : words) {
		 if (fileExtension(filepath).contains(word)) {
			 i=1;
			 }
		 }
		return i;
	}
	
	public static int imageFile(String filepath){
		int i=2;
		//.jpeg, .jpg, .png, .bmp, .gif
		Set<String> words = new HashSet<String>();
	     words.add("jpeg");
	     words.add("jpg");
	     words.add("png");
	     words.add("bmp");	    
	     words.add("gif");	
		 for (String word : words) {
		 if (fileExtension(filepath).contains(word)) {
			 i=1;
			 }
		 }
		return i;
	}
	
	public static int audioFile(String audioPath){
		int i=2;
		//.mp3, .aac, .m4a, .ogg, .oga, .wav
		Set<String> words = new HashSet<String>();
	     words.add("mp3");
	     words.add("aac");
	     words.add("m4a");	    
	     words.add("ogg");	
	     words.add("oga");	
	     words.add("mpeg");	
	     words.add("wav");	
		 for (String word : words) {
		 if (fileExtension(audioPath).contains(word)) {
			 i=1;
			 }
		 }
		return i;
	}
	public static int docFile(String docPath){
		int i=2;
		//.mp3, .aac, .m4a, .ogg, .oga, .wav
		Set<String> words = new HashSet<String>();
	     words.add("doc");
	     words.add("docx");
	     words.add("DOC");	    
	     words.add("DOCX");	
		 for (String word : words) {
		 if (fileExtension(docPath).contains(word)) {
			 i=1;
			 }
		 }
		return i;
	}
	
	public static int xlsFile(String xlsPath){
		int i=2;
		//.mp3, .aac, .m4a, .ogg, .oga, .wav
		Set<String> words = new HashSet<String>();
	     words.add("xls");
	     words.add("xlsx");
	     words.add("XLS");	    
	     words.add("XLSX");	
		 for (String word : words) {
		 if (fileExtension(xlsPath).contains(word)) {
			 i=1;
			 }
		 }
		return i;
	}
	
	public static int pdfFile(String pdfPath){
		int i=2;
		//.mp3, .aac, .m4a, .ogg, .oga, .wav
		Set<String> words = new HashSet<String>();
	     words.add("pdf");
		 for (String word : words) {
		 if (fileExtension(pdfPath).contains(word)) {
			 i=1;
			 }
		 }
		return i;
	}
	
	public static String getMimeType(String url) {
	    String type = null;
	    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
	    if (extension != null) {
	        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	    }
	    return type;
	}
	
	public static int isValidFile(String filePath){
		int i=2;
		//.doc , .docx, .pdf, .xls, .txt, .html, xlsx, .ppt, .pptx + above image/audio/video
		Set<String> words = new HashSet<String>();
	     words.add("doc");
	     words.add("docx");
	     words.add("pdf");	    
	     words.add("xls");	
	     words.add("xlsx");	
	     words.add("ppt");
	     words.add("pptx");	
	     words.add("txt");	
	     words.add("html");	
	     words.add("xml");	
		 if (audioFile(filePath)==1||imageFile(filePath)==1||videoFile(filePath)==1) {
			i=1;
		 }else {
			for (String word : words) {
				if (fileExtension(filePath).contains(word)) {
					i=1;
					}
				}
		 	}
		return i;
	}
	private static String fileExtension(String filePath){
		String ext = MimeTypeMap.getFileExtensionFromUrl(filePath);
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
		if (type==null) {
			ext = filePath.substring(filePath.lastIndexOf("."));
		}
		return ext;
	}
	public static int fileFormat(String file_path){
		int format_value = 0;
		if (isValidDocument(fileExtension(file_path))==true) {
			format_value = 1;
		}else if (isValidPpt(fileExtension(file_path))==true) {
			format_value = 2;
		}else if (fileExtension(file_path).equalsIgnoreCase("pdf")) {
			format_value = 3;
		}else if (isValidWeb(fileExtension(file_path))==true) {
			format_value = 4;
		}
		return format_value;
	}
	
	private static boolean isValidDocument(String ext){
		Set<String> words = new HashSet<String>();
	     words.add("doc");
	     words.add("docx");
	     words.add("odt");	    
	     words.add("rtf");	
	     words.add("ott");	
	     for (String word : words) {
	    	 if (ext.equalsIgnoreCase(word)) {
	    		 return true;
	    	 }
	     }	
	     return false;
	}
	
	private static boolean isValidPpt(String ext){
		Set<String> words = new HashSet<String>();
	     words.add("ppt");
	     words.add("pptx");
	     words.add("otp");	    
	     words.add("odp");	
	     words.add("pps");	
	     for (String word : words) {
	    	 if (ext.contains(word)) {
	    		 return true;
	    	 }
	     }	
	     return false;
	}
	
	private static boolean isValidWeb(String ext){
		Set<String> words = new HashSet<String>();
	     words.add("html");
	     words.add("xml");
	     words.add("xhtml");	    
	     words.add("php");	
	     for (String word : words) {
	    	 if (ext.contains(word)) {
	    		 return true;
	    	 }
	     }	
	     return false;
	}
}
