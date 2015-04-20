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

public class Range {
	
	private Filter filter1;
	private Filter filter2;
	private Order order;
	private boolean usedInNonEqRestart;
	
	public Range() {
	}
	
	public Range(Range base) {
		filter1 = base.filter1;
		filter2 = base.filter2;
		order = base.order;
	}
	
	public Range(Filter filter1) {
		this.filter1 = filter1;
	}
	
	public Range(Order order) { //phony, implicit
		Operator operator = order.getOrderType().equals(OrderType.ASC) ?
				Operator.gt : Operator.lt;
		// the parameter is not known at this time
		filter1 = new Filter(order.getProperty(), operator, null);
		this.order = order;
	}

	public Filter getFilter1() {
		return filter1;
	}

	public Filter getFilter2() {
		return filter2;
	}

	public Order getOrder() {
		return order;
	}

	public boolean isUsedInNonEqRestart() {
		return usedInNonEqRestart;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setFilter2(Filter filter2) {
		this.filter2 = filter2;
	}

	public void setUsedInNonEqRestart(boolean usedInNonEqRestart) {
		this.usedInNonEqRestart = usedInNonEqRestart;
	}
}
