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

package edu.ucdenver.ccp.common.exception.runtime;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class MultipleFailuresException extends RuntimeException {

	private final String message;
	private final List<Throwable> failures;

	public MultipleFailuresException(String message, Throwable... throwables) {
		this.message = message;
		failures = Arrays.asList(throwables);
	}

	public String getMessage() {
		return message;
	}

	public List<Throwable> getFailures() {
		return failures;
	}
	
}
