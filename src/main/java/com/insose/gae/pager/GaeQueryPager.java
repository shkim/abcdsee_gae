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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;

/**
 * Generic Forward and Backward Bookmark paging for Google App Engine for Java.<br>
 * <br>
 * At the present time, the Google App Engine does not provide a built-in
 * pagination support. There are various methods how one can implement limited
 * pagination manually, but writing the paging queries by hand can be tedious
 * and error-prone. Thus this pager tries to alleviate this problem by providing
 * an automatic way to generate appropriate paging queries from a base query,
 * and then using these queries to page through the data.<br>
 * <br>
 * The pagination method used is the one described in this document:
 * http://google-appengine.googlegroups.com/web/
 * efficient_paging_using_key_instead_of_a_dedicated_unique_property.txt<br>
 * However the algorithm used was developed from scratch.<br>
 * <br>
 * This pagination method can not be used for a direct jump to a specific page
 * number. Only continuous forward and backward paging using a bookmark and an
 * unique property is supported.<br>
 * <br>
 * Due to the fact that the number of composite indexes allowed on the Google
 * App Engine is limited to 100 per app, it is recommended to optimize your
 * queries to have lower composite index requirements. If you use an ordering on
 * a property and the values of this property are not unique, it is always
 * better to have a copy of this property with these values made unique - that
 * way multiple queries will not have to be used, and the composite index
 * requirements will be much lower.<br>
 * <br>
 * The pager always returns JDO data class instances, however if you use the
 * low-level Java datastore API to efficiently implement optimization techniques
 * using ancestor entities, you can construct a low-level API GaeQuery, and the
 * pager will use that query for paging on the ancestor table, but it will
 * automatically return JDO instances discovered by calling getParent() on the
 * resulting ancestor entities.<br>
 * <br>
 * A GaeQuery instance can be constructed automatically by the pager, using a
 * limited subset of the JDOQL query language, or it can be constructed
 * programatically. GaeQuery is a standalone class that can be easily converted
 * to a JDO API query, or to the low-level API query, including the actual
 * parameter values.<br>
 * <br>
 * The code used to test the pager was not included intentionally - a proper
 * Unit tests have to be written. <br>
 * To refresh a view to a page, simply reverse the direction and repeat the
 * call.
 */
public class GaeQueryPager<T> {
	
	private static final Logger logger = Logger
			.getLogger(GaeQueryPager.class.getName());
	
	private PersistenceManager manager;
	private String baseQueryString;
	private int pageSize;
	private boolean starting;
	private boolean hasNextPage;
	
	private LinkedList<GaeQuery> queries = new LinkedList<GaeQuery>();
	private GaeQuery baseQuery;

	/**
	 * Constructor for basic paging on a JDO type. The base query is constructed
	 * from a string literal and a parameter map.
	 * 
	 * @param manager If you want to use the pager to perform the queries,
	 *            otherwise can be null.
	 * @param kind The JDO kind to operate on.
	 * @param pkeyName Name of the unique property, it doesn't have to be the
	 *            primary key.
	 * @param pageSize The size of resulting pages.
	 * @param direction The direction to page. This affects the generated
	 *            queries.
	 * @param baseQueryString The base query expressed in subset of JDOQL. See
	 *            GaeQuery for syntax.
	 * @param paramVals Map with values for the parameters declared in the
	 *            query.
	 * @param bookmark This map is managed by the pager. It must be empty at the
	 *            start of the paging.
	 */
	public GaeQueryPager(
			PersistenceManager manager, 
			Class<T> kind,
			String pkeyName, 
			int pageSize, 
			PageDirection direction,
			String baseQueryString, 
			Map<String, Object> paramVals,
			Map<String, Object> bookmark) {

		this.manager = manager;
		this.pageSize = pageSize;
		this.baseQueryString = baseQueryString;

		baseQuery = new GaeQuery(kind, pkeyName, direction, baseQueryString,
				paramVals, bookmark, true);
		
		init(direction, bookmark);
	}

	/**
	 * Constructor for basic paging on a JDO type. The base query is constructed
	 * programatically.
	 * 
	 * @param manager If you want to use the pager to perform the queries,
	 *            otherwise can be null.
	 * @param pageSize The size of resulting pages.
	 * @param direction The direction to page. This affects the generated
	 *            queries.
	 * @param baseQuery The base query.
	 * @param bookmark This map is managed by the pager. It must be empty at the
	 *            start of the paging.
	 */
	public GaeQueryPager(
			PersistenceManager manager, 
			int pageSize, 
			PageDirection direction,
			GaeQuery baseQuery, 
			Map<String, Object> bookmark) {
		
		this.manager = manager;
		this.pageSize = pageSize;
		this.baseQuery = baseQuery;

		init(direction, bookmark);
	}
	
	private void init(PageDirection direction, Map<String, Object> bookmark) {
		
		if(!baseQuery.isPaging())
			throw new RuntimeException("not a paging query");
		
		baseQuery.postInit(direction, bookmark);
		
		starting = bookmark.isEmpty();
		
		// if you are reading this, first have a look at the comment at the
		// bottom of this file, it may help with understanding the terminology
		// used
		
		if(starting) {
			// create starting bookmarkable query
			
			GaeQuery startQuery = new GaeQuery(baseQuery);

			// propagate filters and ranges
			for(Filter filter : baseQuery.getFilters()) {
				startQuery.addFilter(filter.clone());
			}

			// propagate explicit orders
			for(Order order : baseQuery.getOrders()) {
				startQuery.addOrder(order.clone(direction));
			}
			
			// add implicit orders for any explicit ranges without explicit
			// orders
			for(Range range : baseQuery.getRanges()) {
				if(range.getOrder() == null)
					startQuery.addImplicitOrderForRange(range);
			}
			
			// add unique order if none
			startQuery.addUniqueOrderIfNone(null);
			
			queries.add(startQuery);
		}
		else {
			// create restart queries

			Range uniqueRange = null;
			boolean anyRanges = false;
			
			for(Range range : baseQuery.getRanges()) {
				anyRanges = true;
				if(baseQuery.isUniqueProperty(range.getFilter1().getProperty())) {
					uniqueRange = range;
				}
			}
			
			// add 'sole implicit' unique range if needed.
			// unique range is 'sole implicit' if there are no other explicit
			// ranges and no other explicit orders except unique prop order.
			// this sole implicit unique range can then be treated as an
			// explicit unique range.
			
			if(uniqueRange == null) {
				
				List<Order> baseQueryOrders = baseQuery.getOrders();
				boolean onlyUniqueOrder = baseQueryOrders.isEmpty();
				
				if(!onlyUniqueOrder && baseQueryOrders.size() == 1) {
					onlyUniqueOrder = baseQuery.isUniqueProperty(baseQueryOrders.get(0)
							.getProperty());
				}
				
				if(!anyRanges && onlyUniqueOrder) {
					uniqueRange = new Range(); //hollow, init later
				}
			}

			if(uniqueRange != null) {
				// if there's an explicit range based on unique property -->
				// equality restart is not needed:
				
				GaeQuery restartQuery = new GaeQuery(baseQuery);
			
				// propagate filters
				for(Filter filter : baseQuery.getFilters()) {
					if(filter.getOperator().equals(Operator.eq)) {
						restartQuery.addFilter(filter.clone());
					}
				}
				
				// add order on the unique property
				// take the direction from the base query
				// since this is our main (first) order, no other orders apply
				Order uniqueOrder = restartQuery.addUniqueOrderIfNone(baseQuery);
				
				// *add filter(s): propagate the range with bookmark (see below)
				if(uniqueRange.getFilter1() == null)
					uniqueRange = new Range(uniqueOrder); //init the hollow one
				else
					uniqueRange.setOrder(uniqueOrder);
				
				propagateWithBookmark(restartQuery, uniqueRange, 1);
				
				queries.add(restartQuery);
			}
			else {
				// otherwise equality restart is needed:
				
				// scan base query and prepare a list of explicit and implicit
				// ranges that need equality restart (those w/ non-unique prop)
				
				Set<Order> rangeOrders = new LinkedHashSet<Order>();
				List<Range> ranges = new LinkedList<Range>();
				
				// first gather explicit ranges
				for(Range range : baseQuery.getRanges()) {
					
					if(!baseQuery.isUniqueProperty(range.getFilter1()
							.getProperty())) {
						
						rangeOrders.add(range.getOrder());
						ranges.add(range);
					}
				}
				
				// get explicit orders that don't have explicit ranges; these
				// will need implicit ranges
				List<Order> ordersWithoutRanges = new ArrayList<Order>(
						baseQuery.getOrders());
				
				ordersWithoutRanges.removeAll(rangeOrders);
				
				// add implicit ranges (not for unique orders)
				for(Order order : ordersWithoutRanges) {
					
					if(!baseQuery.isUniqueProperty(order.getProperty())) {
						
						Range range = new Range(order.clone(direction));
						ranges.add(range);
					}
				}
				
				// loop this list in reverse order (rangeA), construct a new query in each iteration
				List<Range> queryLoop = new LinkedList<Range>(ranges);
				Collections.reverse(queryLoop);
				
				// make sure to perform one additional iteration for query with no equalities
				queryLoop.add(null);
				
				for(Range rangeA : queryLoop) {
					
					GaeQuery restartQuery = new GaeQuery(baseQuery);
					int bookmarkCounter = 1;
				
					// propagate filters
					for(Filter filter : baseQuery.getFilters()) {
						
						if(filter.getOperator().equals(Operator.eq)) {
							restartQuery.addFilter(filter.clone());
						}
					}
					
					// start with equality = false for the last iteration
					// otherwise start with true
					boolean equality = rangeA != null;
					
					// loop the above list (rangeB)
					for(Range baseRangeB : ranges) {
						
						Range rangeB = new Range(baseRangeB);
					
						if(equality) {
						
							// add filter: range's property equals bookmark
							restartQuery.addFilter(rangeB.getFilter1()
									.getProperty(), bookmarkCounter++,
									Operator.eq);
						}
						else {
							
							// propagate explicit or add implicit order for this range's property
							if(rangeB.getOrder() == null) {
								// this range needs implicit order
								restartQuery.addImplicitOrderForRange(rangeB);
							}
							else {
								// this range has explicitly defined order
								restartQuery.addOrder(rangeB.getOrder().clone());
							}
							
							// *add filter(s): propagate the range with bookmark (see below)
							// but not if this is an implicit range already used in non-equality restart
							if(!baseRangeB.isUsedInNonEqRestart()) {
								propagateWithBookmark(restartQuery, rangeB, bookmarkCounter++);
								baseRangeB.setUsedInNonEqRestart(true);
							}
						}
						
						if(rangeA == baseRangeB)
							equality = false;
					}
					
					// append unique order if none - as all the props are
					// non-unique
					Order uniqueOrder = restartQuery.addUniqueOrderIfNone(null);
					
					// if this query only contains filters (equality), add a
					// unique half-bound range w/ bookmark (same prop as unique
					// order)
					boolean onlyFilters = true;
					for(Filter filter : restartQuery.getFilters()) {
						if(!filter.getOperator().equals(Operator.eq)) {
							onlyFilters = false;
							break;
						}
					}
					if(onlyFilters) {
						Range range = new Range(uniqueOrder);
						propagateWithBookmark(restartQuery, range,
								bookmarkCounter++);
					}
					
					queries.add(restartQuery);
				}
			}
		}
	}
	
	private void propagateWithBookmark(GaeQuery query, Range range,
			int bookmarkCounter) {
		
		// if this is an explicit non-unique half-bound bookmark range, and we are
		// restarting in reverse direction, we need to make it full bound,
		// using the original inequality filter
		
		if(range.getFilter2() == null && query.getDirection().isBackward()
				&& range.getFilter1().getParameter() != null) {
			
			Filter filter1 = range.getFilter1();
			Filter filter2 = new Filter(filter1.getProperty(), filter1.getOperator(), filter1.getParameter());
			range.setFilter2(filter2);
		}

		if(range.getFilter2() == null) {
			// half-bound:

			// ASC ordered --> prop > bookmark
			// DESC ordered -> prop < bookmark

			Operator operator = range.getOrder().isAsc() ? 
					Operator.gt : Operator.lt;
			
			query.addFilter(range.getFilter1().getProperty(), bookmarkCounter,
					operator);
		}
		else {
			// bound:
			
			// DESC order --> prop < bookmark && prop ORIGINAL_LOWER_BOUND_OPERATOR explicit_lower_bound
			// ASC order  --> prop > bookmark && prop ORIGINAL_UPPER_BOUND_OPERATOR explicit_upper_bound
			
			Operator bookmarkOperator;
			Filter boundFilter;
			
			if(range.getOrder().isDesc()) {
				
				bookmarkOperator = Operator.lt;
				
				boundFilter = !range.getFilter1().getOperator().isLessThan() ? 
						range.getFilter1() : range.getFilter2();
			}
			else {
				
				bookmarkOperator = Operator.gt;
				
				boundFilter = range.getFilter1().getOperator().isLessThan() ? 
						range.getFilter1() : range.getFilter2();
			}
			
			query.addFilter(range.getFilter1().getProperty(), bookmarkCounter, bookmarkOperator);
			query.addFilter(boundFilter);
		}

	}

	/**
	 * Perform the queries, and return the page.
	 * 
	 * @return The JDO items that make up the resulting page.
	 */
	public List<T> performQueries() {

		List<T> results = new LinkedList<T>();
		
		int cnt = 1;
		for(GaeQuery query : queries) {
			
			Map<String, Object> parameters = query.getParameterMap();
			
			String queryString = query.toString();
			
			if(logger.isLoggable(Level.FINE)) {
				logger.info("Executing " + query.getDirection() + " " 
						+ (starting ? "start" : "resume") + " "
						+ (query.isLowLevelApi() ? "low-level" : "JDO")
						+ " query #" + cnt++ + ": " + queryString + " "
						+ parameters);
			}
			
			int recordsToGet = pageSize + 1 - results.size();
			List<T> subResults;
			
			if(query.isLowLevelApi()) {
				
				com.google.appengine.api.datastore.Query llQuery = 
					query.asLowLevelQuery();
				
				DatastoreService datastoreService = DatastoreServiceFactory
					.getDatastoreService();
				
				PreparedQuery preparedQuery = datastoreService.prepare(llQuery);
				
				subResults = (List<T>) preparedQuery.asList(FetchOptions.Builder
						.withLimit(recordsToGet).chunkSize(recordsToGet));
			}
			else {
			
				Query dbQuery = manager.newQuery(queryString);
				dbQuery.setClass(query.getKind());
				dbQuery.setRange(0, recordsToGet);
				subResults = (List<T>) dbQuery.executeWithMap(parameters);
			}
			
			results.addAll(subResults);
			if(subResults.size() == recordsToGet)
				break;
		}
		
		if(results.size() == pageSize + 1) {
			hasNextPage = true;
			results.remove(results.size() - 1);
		}
		else {
			hasNextPage = false;
		}
		
		Map<String, Object> bookmark = baseQuery.getBookmark();
		
		if(!results.isEmpty()) {
			
			// if this was a starting query, get bookmark props by orders
			// otherwise get bookmarks from the first resume query

			List<String> bookmarkProps = new LinkedList<String>();
			GaeQuery query = queries.get(0);
			
			if(starting) {
				for(Order order : query.getOrders()) {
					bookmarkProps.add(order.getProperty());
				}
			}
			else {
				for(Filter filter : query.getFilters()) {
					if(filter.getParameter().isBookmark()) {
						bookmarkProps.add(filter.getProperty());
					}
				}
			}

			if(queries.get(0).getDirection().isBackward())
				Collections.reverse(results);

			T toBookmarkNext = results.get(results.size() - 1);
			T toBookmarkPrev = results.get(0);
			
			for(String prop : bookmarkProps) {
				Object value;
				if(baseQuery.isLowLevelApi()) {
					value = ((Entity) toBookmarkNext).getProperty(prop);
				}
				else {
					value = Utils.reflexiveGet(toBookmarkNext, prop);
				}
				bookmark.put(prop + "." + PageDirection.forward, value);
			}
			
			for(String prop : bookmarkProps) {
				Object value;
				if(baseQuery.isLowLevelApi()) {
					value = ((Entity) toBookmarkPrev).getProperty(prop);
				}
				else {
					value = Utils.reflexiveGet(toBookmarkPrev, prop);
				}
				bookmark.put(prop + "." + PageDirection.backward, value);
			}
		}
		
		Class<?> relaKind = baseQuery.getParentIndexKind();
		if(relaKind != null) {
			
			List<Long> ids = new LinkedList<Long>();
			
			for(Entity entity : (List<Entity>) results) {
				long id = entity.getKey().getParent().getId();
				ids.add(id);
			}
			
			results = (List<T>) manager.getObjectsById(ids);
		}
		
		return results;
	}
	
	/**
	 * Perform the count query.
	 * 
	 * @return Number of items returned by the base query. Can be used to
	 *         compute the number of pages.
	 */
	public long performCount() {
		
		String countQueryString = baseQuery.toString(false, false);
		Map<String, Object> parameters = baseQuery.getParameterMap();

		if(logger.isLoggable(Level.FINE))
			logger.info("Executing count query: " + countQueryString + " "
					+ parameters);
		
		Query countQuery = manager.newQuery(countQueryString);
		countQuery.setClass(baseQuery.getKind());
		countQuery.setResult("count(this)");
		
		Number count = (Number) countQuery.executeWithMap(parameters);
		return count.longValue();
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	
	/**
	 * Test, whether the next invocation of this base query, in the same
	 * direction, will yield any results.
	 * 
	 * @return True if there are more pages in this direction.
	 */
	public boolean hasNextPage() {
		return hasNextPage;
	}
	
	public String toString(boolean terse) {
		
		StringBuilder sb = new StringBuilder();

		if(!terse) {
			sb.append("Kind:         ").append(
					baseQuery.getKind().getSimpleName()).append('\n');
			sb.append("Page size:    ").append(pageSize).append('\n');
			sb.append("Bookmark:     ").append(baseQuery.getBookmark()).append(
					'\n');
			sb.append("Input string: ").append(baseQueryString).append('\n');
		}

		sb.append("Base query:  ").append(baseQuery.toString(terse, true))
				.append('\n');

		if(baseQuery.getBookmark().isEmpty()) {
			sb.append("Bookmarkable: ").append(
					queries.get(0).toString(terse, true)).append('\n');
		}
		else {
			int i = 1;
			for(GaeQuery resumeQuery : queries) {
				sb.append("Resumable #").append(i++).append(": ").append(
						resumeQuery.toString(terse, true)).append('\n');
			}
		}
		
		return sb.toString();
	}
	
	public static void basicTest() throws Exception {
		
		String testTable[] = new String[] { 
			"",
			"WHERE x == p1 PARAMETERS int p1",
			"WHERE x > p1 PARAMETERS int p1",
			"WHERE x < p1 PARAMETERS int p1",
			"WHERE x == p1 && y > p2 PARAMETERS int p1, int p2",
			"WHERE x > p1 && x < p2 PARAMETERS int p1, int p2",
			"WHERE __key__ > P1 PARAMETERS int P1",
			"WHERE __key__ < P1 PARAMETERS int P1",
			"WHERE __key__ > P1 && __key__ < P2 PARAMETERS int P1, int P2",
			"ORDER BY x ASC",
			"ORDER BY x DESC",
			"ORDER BY __key__ ASC",
			"ORDER BY __key__ DESC",
			"ORDER BY x ASC, y DESC",
			"ORDER BY x ASC, z ASC, y DESC",
			"ORDER BY x ASC, __key__ DESC",
			"WHERE x == p1 PARAMETERS int p1 ORDER BY y DESC",
			"WHERE x > p1 && x < p2 PARAMETERS int p1, int p2 ORDER BY x DESC"
		};
		
		List<Object[]> rows = new LinkedList<Object[]>();
		int maxlen1 = 0, maxlen2 = 0;
		
		for(String query : testTable) {
			
			Map<String, Object> paramValMap = new LinkedHashMap<String, Object>();
			paramValMap.put("p1", 0);
			paramValMap.put("p2", 9);
			paramValMap.put("P1", "A");
			paramValMap.put("P2", "Z");

			Map<String, Object> bookmark = new LinkedHashMap<String, Object>();

			GaeQueryPager<Object> pagerInitial = new GaeQueryPager<Object>(
					null, Object.class, "__key__", 10, PageDirection.forward,
					query, paramValMap, bookmark);

			GaeQueryPager<Object> pagerInitialBackward = new GaeQueryPager<Object>(
					null, Object.class, "__key__", 10, PageDirection.backward,
					query, paramValMap, bookmark);

			bookmark.put("__key__", "jek3js8e2");
			bookmark.put("x", 0);
			bookmark.put("y", 9);
			bookmark.put("z", 7);
			bookmark.put("A", "AA");
			bookmark.put("Z", "ZZ");
			
			GaeQueryPager<Object> pagerResume = new GaeQueryPager<Object>(
					null, Object.class, "__key__", 10, PageDirection.forward,
					query, paramValMap, bookmark);

			GaeQueryPager<Object> pagerResumeBackward = new GaeQueryPager<Object>(
					null, Object.class, "__key__", 10, PageDirection.backward,
					query, paramValMap, bookmark);

			Object row[] = new Object[] {
					pagerInitial.baseQuery.toString(true, true),
					
					pagerInitial.queries.get(0).toString(true, true),
					pagerResume,
					
					pagerInitialBackward.queries.get(0).toString(true, true),
					pagerResumeBackward
			};
			maxlen1 = Math.max(maxlen1, ((String) row[0]).length());
			maxlen2 = Math.max(maxlen2, ((String) row[1]).length());
			maxlen2 = Math.max(maxlen2, ((String) row[3]).length());
			rows.add(row);
		}
		
		String pad = "                                              ";
		pad = pad + pad + pad + pad + pad;
		
		for(Object row[] : rows) {
			
			toStringQuery(maxlen1, maxlen2, pad, row, true);
			toStringQuery(maxlen1, maxlen2, pad, row, false);
		}
	}

	private static void toStringQuery(int maxlen1, int maxlen2, String pad,
			Object[] row, boolean forward) {

		String SEP = forward ? " |F| " : " |B| ";
		
		String s1 = (String) row[0];
		String s2 = (String) row[forward ? 1 : 3];
		GaeQueryPager<Object> pager = (GaeQueryPager<Object>) row[forward ? 2 : 4];
		StringBuilder sb = new StringBuilder();
		sb.append(SEP);
		sb.append(s1 + pad.substring(0, maxlen1 - s1.length()));
		int tab1 = sb.length() - 5;
		sb.append(SEP);
		sb.append(s2 + pad.substring(0, maxlen2 - s2.length()));
		int tab2 = sb.length() - 10;
		sb.append(SEP);
		
		boolean first = true;
		for(GaeQuery query : pager.queries) {
			String qs = query.toString(true, true);
			if(first) {
				sb.append(qs).append('\n');
				first = false;
			}
			else {
				sb.append(SEP).append(pad.substring(0, tab1));
				sb.append(SEP).append(pad.substring(0, tab2 - tab1));
				sb.append(SEP).append(qs).append('\n');
			}
		}
		System.out.println(sb.toString());
	}

	public GaeQuery getStartingQuery() {
		if(!starting)
			throw new RuntimeException("not a starting pager");
		return queries.get(0);
	}

	public LinkedList<GaeQuery> getQueries() {
		return queries;
	}
}

/*

query element classification:

	equality --> filter		simply propagates to all queries

	inequality --> range	ALWAYS SORTED (AND FIRST)  [always mappable to a single property]
			if not unique, requires range restart first on equals, then in correct direction, and optional end bound
																		> ASC, < DESC, if can't decide, use ASC
			bound range: x > 0 && x < 9
			half-bound range: x > 5
																	
	order by x asc --> same as --> (x > (min_x_val - 1))  'boundless range'
	order by x desc --> same as --> (x < (max_x_val + 1))  'boundless range'

	unique inequality --> as inequality, but restart without equals

	unique order is appended to order unordered non-uniqueness (duplicate values)

	on restart with more ranges, keep removing their equalities starting from most local one (reverse order)


	explicit vs implicit     (explicit ranges always sorted before implicit ranges)


	query = filter, range(half-bound, bound, boundless), order


	restart context:

		explicit order --requires--> explicit range (user prop) OR implicit half-bound range (unique prop)
		
		explicit range(bound, half-bound) --requires--> explicit order (user direction) OR implicit order (derived direction)

	start context:
	
		explicit order --> is propagated

		explicit range(bound, half-bound) --requires--> explicit order (user direction) OR implicit order (derived direction)

	deriving implicit order for start query from a range:
		i > 0				ASC
		i < 9				DESC
		i > 0 && i < 9		ASC

*/