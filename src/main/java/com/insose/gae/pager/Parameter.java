/*
 * Copyright (c) 2009, Juraj Vitko, insose.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY Juraj Vitko ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Juraj Vitko BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.insose.gae.pager;

public class Parameter {
	
	private String name;
	private Class<?> type;
	private Object value;
	private boolean bookmark;
	
	Parameter(String name) {
		this.name = name;
	}
	
	public Parameter(String name, Object value) {
		this.name = name;
		this.value = value;
		if(value == null)
			throw new RuntimeException("cannot derive type from null " +
					"value, use other constructor");
		this.type = value.getClass();
	}
	
	public Parameter(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

	public Parameter(String name, Class<?> type, Object value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public Parameter clone() {
		Parameter parameter = new Parameter(name, type, value);
		parameter.bookmark = bookmark;
		return parameter;
	}

	void learnFrom(Parameter other) {
		if(other.type != null)
			this.type = other.type;
		if(other.value != null)
			this.value = other.value;
		if(other.bookmark)
			this.bookmark = true;
	}

	void setType(Class<?> type) {
		this.type = type;
	}
	
	public String toString() {
		return toString(true);
	}
	
	public String toString(boolean decl) {
		StringBuilder sb = new StringBuilder();
		if(decl)
			sb.append(type.getName()).append(' ');
		return sb.append(name).toString();
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isBookmark() {
		return bookmark;
	}

	void setBookmark(boolean bookmark) {
		this.bookmark = bookmark;
	}
}
