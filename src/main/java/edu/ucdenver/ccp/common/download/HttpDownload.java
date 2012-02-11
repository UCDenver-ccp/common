/* Copyright (C) 2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP common library.
 * The CCP common library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.ucdenver.ccp.common.download;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Definition for the @HttpDownload annotation. This annotation facilitates the download of a
 * particular file via HTTP and the referencing of that file to a File member variable in a class.
 * The @HttpDownload annotation is "activated" by calling the DownloadUtil.download() method.
 * 
 * @author bill
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface HttpDownload {
	/**
	 * The URL where the file to download is located
	 * 
	 * @return
	 */
	String url();

	/**
	 * Optional parameter allowing the user to specify the name of the file once it is downloaded
	 * and saved locally. If not used an attempt to infer the file name is made by looking at the
	 * contents of the URL after the final forward slash.
	 * 
	 * @return
	 */
	String fileName() default "";
	
	/**
	 * The name of the file to retrieve from inside a zip archive (if the downloaded file is a zip
	 * archive)
	 * 
	 * @return
	 */
	String targetFileName() default "";
}
