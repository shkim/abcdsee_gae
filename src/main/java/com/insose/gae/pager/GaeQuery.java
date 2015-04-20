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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

/**
 * Standalone representation of a query. The query can be built using a string
 * literal of a limited JDOQL syntax, or programatically.
 */
public class GaeQuery {
	
	private List<Filter> filters = new LinkedList<Filter>();
	private List<Order> orders = new ArrayList<Order>();
	private Class<?> kind;
	private String entityKind;
	private String uniquePropertyName;
	private Map<String, Object> paramVals;
	private Map<String, Object> bookmark;
	private PageDirection direction;
	private boolean paging;
	private boolean lowLevelApi;
	private Class<?> parentIndexKind;
	
	/**
	 * Create an empty JDO query that will be configured programatically.
	 * 
	 * @param kind The base JDO kind.
	 * @param uniquePropertyName Name of the unique property, it doesn't have to be the primary key.
	 * @param paging Whether paging should be used. The pager only accepts paging queries.
	 */
	public GaeQuery(Class<?> kind, String uniquePropertyName, boolean paging) {
		
		this.kind = kind;
		this.uniquePropertyName = uniquePropertyName;
		this.paging = paging;
	}

	/**
	 * Create an empty low-level API query, to be configured programatically.
	 * 
	 * @param entityKind The kind of the low-level Entity. Instances of this Entity must be ancestors of the JDO parentIndexKind parameter.
	 * @param uniquePropertyName Name of the unique property, it doesn't have to be the primary key.
	 * @param paging Whether paging should be used. The pager only accepts paging queries.
	 * @param parentIndexKind The JDO kind the pager will return, when executing this query.
	 */
	public GaeQuery(String entityKind, String uniquePropertyName, boolean paging, Class<?> parentIndexKind) {
		
		this.kind = Object.class;
		this.entityKind = entityKind;
		this.uniquePropertyName = uniquePropertyName;
		this.paging = paging;
		this.lowLevelApi = true;
		this.parentIndexKind = parentIndexKind;
	}

	/**
	 * Create a new query based on an existing one. Only base parameters are reused. The Orders and Filters must be configured from scratch.
	 * 
	 * @param base The GaeQuery to base this new query on.
	 */
	public GaeQuery(GaeQuery base) {
		
		this.kind = base.kind;
		this.uniquePropertyName = base.uniquePropertyName;
		this.paramVals = base.paramVals;
		this.bookmark = base.bookmark;
		this.direction = base.direction;
		this.paging = base.paging;
		this.lowLevelApi = base.lowLevelApi;
		this.entityKind = base.entityKind;
		this.parentIndexKind = base.parentIndexKind;
	}
	
	/**
	 * Create a new query from a limited substet of JDOQL syntax.<br>
	 * <br>
	 * <b>query:</b><br><code>[[WHERE <b>filters</b>] [PARAMETERS <b>parameters</b>]] [ORDER BY <b>orders</b>]</code><br>
	 * <b>filters:</b><br><code>filter [&& filters]</code><br>
	 * <b>filter:</b><br><code>properyName operator parameterName</code><br>
	 * <b>operator:</b><br>one of: <code>&lt; &lt;= == &gt; &gt;=</code><br>
	 * <b>parameters:</b><br><code>parameter [, parameters]</code><br>
	 * <b>parameter:</b><br><code>javaTypeName parameterName</code><br>
	 * <b>orders:</b><br><code>order [, orders]</code><br>
	 * <b>order:</b><br><code>propertyName [ASC | DESC]</code><br>
	 * 
	 * @param kind The base JDO kind.
	 * @param uniquePropertyName Name of the unique property, it doesn't have to be the primary key.
	 * @param direction The direction the pager will use.
	 * @param queryString The query string.
	 * @param paramVals Map with values for the parameters declared in the query.
	 * @param bookmark This map is managed by the pager. It must be empty at the start of the paging.
	 * @param paging Whether paging should be used. The pager only accepts paging queries.
	 */
	public GaeQuery(Class<?> kind, String uniquePropertyName,
			PageDirection direction, String queryString,
			Map<String, Object> paramVals, Map<String, Object> bookmark,
			boolean paging) {
		
		this.kind = kind;
		this.uniquePropertyName = uniquePropertyName;
		this.paramVals = paramVals;
		this.bookmark = bookmark;
		this.direction = direction;
		this.paging = paging;
		
		ParseIterator parseIterator = new ParseIterator(queryString);
		
		if(parseIterator.hasNext()) {
			String s = parseIterator.next();
			if(s.toLowerCase().equals("where")) {
				parseFilters(parseIterator);
			}
			else if(s.toLowerCase().equals("order by")) {
				parseOrders(parseIterator);
			}
			else if(s.length() > 0) {
				parseIterator.error("Unexpected: " + s);
			}
		}
	}

	public GaeQuery clone() {
		GaeQuery clone = new GaeQuery(this);
		
		for(Filter filter : filters)
			clone.addFilter(filter.clone());
		
		for(Order order : orders)
			clone.addOrder(order.clone());
		
		return clone;
	}

	private void setParameterValues() {
		
		for(Filter filter : filters) {
			
			Parameter parameter = filter.getParameter();
			
			if(parameter.getType() == null)
				throw new RuntimeException("undeclared parameter: "
						+ parameter.getName());
			
			if(parameter.getValue() == null) {
				
				Object value;
				
				if(parameter.isBookmark()) {

					String propName = filter.getProperty() + "." + direction;
					
					if(!bookmark.containsKey(propName)) {
						throw new RuntimeException("bookmark must contain "
								+ propName);
					}
					value = bookmark.get(propName);

				}
				else {
					String paramName = parameter.getName();
					
					if(!paramVals.containsKey(paramName)) {
						throw new RuntimeException(
								"missing parameter value for: " + paramName);
					}
					value = paramVals.get(paramName);
				}
				
				parameter.setValue(value);
			}
		}
	}
	
	private Operator parseOperator(ParseIterator parseIterator) {
		return Operator.parse(parseIterator.next("expected: an operator"));
	}
	
	private void parseParameterDecl(ParseIterator parseIterator) {
		
		String typeName = parseIterator.next("parameter type name expected");
		String name = parseIterator.next("parameter name expected");
		
		Class<?> type = Utils.classForName(typeName);
		if(type == null)
			parseIterator.error("unknown parameter type: " + typeName);
		
		Parameter parameter = findParameterByName(name);
		if(parameter == null)
			throw new RuntimeException("parameter declared " +
					"but not used in query: " + name);
		
		parameter.setType(type);
	}
	
	public Parameter findParameterByName(String name) {
		
		for(Filter filter : filters) {
			if(filter.getParameter().getName().equals(name)) {
				return filter.getParameter();
			}
		}
		
		return null;
	}
	
	private void parseFilters(ParseIterator parseIterator) {
		
		parseFilter(parseIterator);
		
		if(parseIterator.hasNext()) {
			String s = parseIterator.next();
			if(s.toLowerCase().equals("order by")) {
				parseOrders(parseIterator);
			}
			else if(s.equals("&&")) {
				parseFilters(parseIterator);
			}
			else if(s.toLowerCase().equals("parameters")) {
				parseParameterDecls(parseIterator);
			}
			else {
				throw new RuntimeException("unexpected: " + s);
			}
		}
	}
	
	private void parseOrders(ParseIterator parseIterator) {

		parseOrder(parseIterator);
	
		if(parseIterator.hasNext()) {
			parseOrders(parseIterator);
		}
	}

	private void parseParameterDecls(ParseIterator parseIterator) {
		
		parseParameterDecl(parseIterator);
		
		if(parseIterator.hasNext()) {
			
			String s = parseIterator.next();
			
			if(s.toLowerCase().equals("order by")) {
				parseOrders(parseIterator);
			}
			else {
				parseIterator.stepBack();
				parseParameterDecls(parseIterator);
			}
		}
	}

	private Filter parseFilter(ParseIterator parseIterator) {
		
		String property = parseIterator.next("filter property name expected");
		Operator operator = parseOperator(parseIterator);
		String parameterName = parseIterator.next("parameter name expected");
		
		Filter filter = new Filter(property, operator, new Parameter(parameterName));
		addFilter(filter);
		return filter;
	}
	
	private Order parseOrder(ParseIterator parseIterator) {
		
		String property = parseIterator.next("expected: order property name");
		OrderType orderType = OrderType.valueOf(parseIterator.next(
				"expected: ASC or DESC").toUpperCase());
		
		Order order = new Order(property, orderType);
		addOrder(order);
		return order;
	}
	
	public void addFilter(Filter filter) {
		
		Parameter parameter = findParameterByName(filter.getParameter().getName());
		
		if(parameter != null && parameter != filter.getParameter()) {
			parameter.learnFrom(filter.getParameter());
			filter.setParameter(parameter);
		}
		
		filters.add(filter);
	}

	public void addOrder(Order order) {
		orders.add(order);
	}

	void addFilter(String propName, int bookmarkCounter, Operator operator) {
		
		String bookmarkId = "B" + bookmarkCounter;
		
		Class<?> type;
		
		if(kind.equals(Object.class)) {
			//testing support
			type = Object.class;
		}
		else {
			Field field = Utils.findClassField(kind, propName);
			type = field.getType();
		}

		Parameter parameter = new Parameter(bookmarkId, type);
		parameter.setBookmark(true);
		Filter filter = new Filter(propName, operator, parameter);
		
		addFilter(filter);
	}
	
	void addImplicitOrderForRange(Range range) {
		
		// add an implicit order, but make sure to add it as the first one,
		// since inequality filters is what we call ranges,
		// and these need to be ordered first
		
		OrderType orderType = range.getFilter1().getOperator().isLessThan() ? 
				OrderType.DESC : OrderType.ASC;
		
		if(direction.isBackward()) {
			orderType = orderType.equals(OrderType.ASC) ? 
					OrderType.DESC : OrderType.ASC;
		}
		
		Order order = new Order(range.getFilter1().getProperty(), orderType);
		orders.add(0, order);
		range.setOrder(order);
	}
	
	boolean isUniqueProperty(String name) {
		return uniquePropertyName.equals(name);
	}
	
	public Order findOrderByProperty(String property) {
		for(Order order : orders) {
			if(order.getProperty().equals(property)) {
				return order;
			}
		}
		return null;
	}

	public Parameter findParameterByPropertyName(String property) {
		
		for(Filter filter : filters) {
			if(filter.getProperty().equals(property)) {
				if(filter.getParameter() != null)
					return filter.getParameter();
			}
		}
		return null;
	}

	Order addUniqueOrderIfNone(GaeQuery baseQuery) {
		
		Order baseQueryUniqueOrder = baseQuery != null ? 
				baseQuery.findOrderByProperty(uniquePropertyName) : null;
		
		Order pkeyOrder = findOrderByProperty(uniquePropertyName);
		
		if(pkeyOrder == null) {
			
			OrderType orderType;
			
			if(baseQueryUniqueOrder != null) {
				orderType = baseQueryUniqueOrder.getOrderType();
			}
			else {
				orderType = OrderType.ASC;
			}
			
			if(direction.isBackward()) {
				if(orderType.equals(OrderType.ASC))
					orderType = OrderType.DESC;
				else
					orderType = OrderType.ASC;
			}
			
			pkeyOrder = new Order(uniquePropertyName, orderType);
			addOrder(pkeyOrder);
		}
		else {
			if(baseQueryUniqueOrder != null) {
				pkeyOrder.setOrderType(baseQueryUniqueOrder.getOrderType());
			}
		}
		
		// move unique order to the end, if it's not at end
		// this works for both starting, resume-no-equality and resume-quality
		// queries, because in the resume-no-equality the unique order is the
		// only one
		int sz = orders.size();
		if(sz > 1 && pkeyOrder != orders.get(sz - 1)) {
			orders.remove(pkeyOrder);
			orders.add(pkeyOrder);
		}
		return pkeyOrder;
	}
	
	Collection<Range> getRanges() {
		
		Map<String, Range> ranges = new LinkedHashMap<String, Range>();
		for(Filter filter : filters) {
			
			if(!filter.getOperator().equals(Operator.eq)) {
				
				Range range = ranges.get(filter.getProperty());
				if(range == null) {
					
					range = new Range(filter);
					
					for(Order order : orders) {
						if(order.getProperty().equals(filter.getProperty())) {
							range.setOrder(order);
						}
					}
					
					ranges.put(filter.getProperty(), range);
				}
				else {
					range.setFilter2(filter);
				}
			}
		}
		return ranges.values();
	}
	
	Map<String, Object> getParameterMap() {
		
		setParameterValues();
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		for(Filter filter : filters) {
			
			map.put(filter.getParameter().getName(), filter.getParameter()
					.getValue());
		}
		
		return map;
	}
	
	@Override
	public String toString() {
		return toString(false, true);
	}
	
	public String toString(boolean terse, boolean ordering) {
		
		boolean first;
		StringBuilder sb = new StringBuilder();
		if(!terse) {
			sb.append("SELECT ");
		}
		
		if(!filters.isEmpty()) {
			sb.append("WHERE ");
			first = true;
			for(Filter filter : filters) {
				if(first)
					first = false;
				else
					sb.append(" && ");
				sb.append(filter);
			}
		}
		
		if(!terse) {
			
			Set<Parameter> usedParameters = new LinkedHashSet<Parameter>();
			for(Filter filter : filters) {
				usedParameters.add(filter.getParameter());
			}
			
			if(!usedParameters.isEmpty()) {
				
				if(sb.length() > 0)
					sb.append(' ');
				
				sb.append("PARAMETERS ");
				
				first = true;
				for(Parameter parameter : usedParameters) {
					if(first)
						first = false;
					else
						sb.append(", ");
					sb.append(parameter.toString(true));
				}
			}
		}
		
		if(ordering && !orders.isEmpty()) {
			
			if(sb.length() > 0)
				sb.append(' ');
			
			sb.append("ORDER BY ");
			
			first = true;
			for(Order order : orders) {
				if(first)
					first = false;
				else
					sb.append(", ");
				sb.append(order);
			}
		}
		
		return sb.toString();
	}

	public Class<?> getKind() {
		return kind;
	}

	public Map<String, Object> getParamVals() {
		return paramVals;
	}

	public Map<String, Object> getBookmark() {
		return bookmark;
	}

	public List<Filter> getFilters() {
		return Collections.unmodifiableList(filters);
	}

	public List<Order> getOrders() {
		return Collections.unmodifiableList(orders);
	}

	public PageDirection getDirection() {
		return direction;
	}

	void postInit(PageDirection direction, Map<String, Object> bookmark) {
		this.direction = direction;
		this.bookmark = bookmark;
	}

	public boolean isPaging() {
		return paging;
	}

	/**
	 * Do not use. The algorithm for determining what indexes are needed is not
	 * properly documented, therefore more testing is necessary before this
	 * function is flawless.
	 */
	@Deprecated
	public List<IndexProperty> getCompositeIndexProperties(String mainKeyName) {
		List<IndexProperty> list = new LinkedList<IndexProperty>();
		
		// all orders (by order's type)
		// all filters, which don't have orders (ASC)
		
		List<Filter> filters2 = new LinkedList<Filter>(filters);
		Collections.reverse(filters2);

		for(Filter filter : filters2) {
			
			if(findOrderByProperty(filter.getProperty()) != null)
				continue;
			
			String prop = filter.getProperty();
			if(prop.equals(mainKeyName))
				prop = "__key__";
			
			list.add(new IndexProperty(prop, OrderType.ASC));
		}

		for(Order order : orders) {
			
			String prop = order.getProperty();
			if(prop.equals(mainKeyName))
				prop = "__key__";
			
			list.add(new IndexProperty(prop, 
					order.getOrderType()));
		}
		
		// no need to declare composite 
		// indexes for a single prop
		if(list.size() == 1)
			list.clear();
		
		return list;
	}

	public String getEntityKind() {
		return entityKind;
	}

	public String getUniquePropertyName() {
		return uniquePropertyName;
	}

	public boolean isLowLevelApi() {
		return lowLevelApi;
	}

	public Class<?> getParentIndexKind() {
		return parentIndexKind;
	}

	public com.google.appengine.api.datastore.Query asLowLevelQuery() {
		
		com.google.appengine.api.datastore.Query llQuery = 
			new com.google.appengine.api.datastore.Query(entityKind);
		
		for(Filter filter : filters) {
			
			FilterOperator llOperator = null;
			switch(filter.getOperator()) {
				case eq:
					llOperator = FilterOperator.EQUAL;
					break;
				case gt:
					llOperator = FilterOperator.GREATER_THAN;
					break;
				case gteq:
					llOperator = FilterOperator.GREATER_THAN_OR_EQUAL;
					break;
				case lt:
					llOperator = FilterOperator.LESS_THAN;
					break;
				case lteq:
					llOperator = FilterOperator.LESS_THAN_OR_EQUAL;
					break;
			}
			
			llQuery.addFilter(filter.getProperty(), 
					llOperator, filter.getParameter().getValue());
		}
		
		for(Order order : orders) {
			llQuery.addSort(order.getProperty(), order.getOrderType().isAsc() ? 
					SortDirection.ASCENDING : SortDirection.DESCENDING);
		}
		
		return llQuery;
	}
}
