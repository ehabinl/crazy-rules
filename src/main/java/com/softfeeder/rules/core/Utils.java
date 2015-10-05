/**
 * The MIT License
 *
 *  Copyright (c) 2015, Ehab Al-Hakawati (e.hakawati@softfeeder.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.softfeeder.rules.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;

/**
 * 
 * @author Ehab Al-Hakawati
 * @since 04-Oct-2015
 *
 */
public class Utils {

	private static final Logger LOG = Logger.getLogger(Utils.class.getName());

	/**
	 * 
	 * @param context
	 * @param target
	 */
	public static void fieldsInjection(RuleContext context, Object target) {
		if (context != null) {
			for (Field field : target.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(Inject.class)) {
					try {
						BeanUtils.setProperty(target, field.getName(), context.getValue(field.getName()));
					} catch (InvocationTargetException | IllegalAccessException ex) {
						LOG.info(String.format("Invalid field injection %s", ex.getMessage()));
					}
				}
			}
		}
	}

}
