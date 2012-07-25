/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.common.download;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.ucdenver.ccp.common.ftp.FTPUtil.FileType;

/**
 * Definition for the @FtpDownload annotation. This annotation facilitates the download of a
 * particular file via FTP and the reference of that file to a File member variable in a class. The @FtpDownload
 * annotation is "activated" by calling the DownloadUtil.download() method.
 * 
 * @author bill
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface FtpDownload {
	/**
	 * The FTP server address, e.g. ftp.ncbi.nih.gov
	 * 
	 * @return
	 */
	String server();

	/**
	 * The FTP port. This option is useful for setting up unit tests using mock ftp servers
	 * 
	 * @return
	 */
	int port() default -1;

	/**
	 * The path (not including the file) where the file to be downloaded can be found on the FTP
	 * server
	 * 
	 * @return
	 */
	String path();

	/**
	 * The name of the file to be downloaded
	 * 
	 * @return
	 */
	String filename();

	/**
	 * The name of the file to retrieve from inside a zip archive (if the downloaded file is a zip
	 * archive)
	 * 
	 * @return
	 */
	String targetFileName() default "";

	/**
	 * Specifies the download mode: ASCII or BINARY
	 * 
	 * @return
	 */
	FileType filetype();

	/**
	 * Specifies the user name to use when logging into the FTP server. Default="anonymous"
	 * 
	 * @return
	 */
	String username() default "anonymous";

	/**
	 * Specifies the password to use when logging into the FTP server. Default="anonymous"
	 * 
	 * @return
	 */
	String password() default "anonymous";
}
