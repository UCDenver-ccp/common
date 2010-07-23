package edu.ucdenver.ccp.common.download;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.ucdenver.ccp.common.ftp.FTPUtil.FileType;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FtpDownload {
	String server();

	int port() default -1;

	String path();

	String filename();

	FileType filetype();

	String username() default "anonymous";

	String password() default "anonymous";
}
