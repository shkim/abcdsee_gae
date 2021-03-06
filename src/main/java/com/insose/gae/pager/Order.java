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

public class Order {
	
	private String property;
	private OrderType orderType;
	
	public Order(String property, OrderType orderType) {
		this.property = property;
		this.orderType = orderType;
	}

	@Override
	protected Order clone() {
		return new Order(property, orderType);
	}

	protected Order clone(PageDirection direction) {
		
		OrderType cloneOrderType;
		
		if(direction.isForward()) {
			cloneOrderType = orderType;
		}
		else {
			cloneOrderType = isAsc() ? 
					OrderType.DESC : OrderType.ASC;
		}
		
		return new Order(property, cloneOrderType);
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(property).append(' ').append(
				orderType).toString();
	}

	public String getProperty() {
		return property;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	
	public boolean isAsc() {
		return orderType.equals(OrderType.ASC);
	}

	public boolean isDesc() {
		return orderType.equals(OrderType.DESC);
	}
}
