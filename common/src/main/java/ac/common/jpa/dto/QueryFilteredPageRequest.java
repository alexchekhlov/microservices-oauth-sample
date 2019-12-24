package ac.common.jpa.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Request object to get pages of data")
public class QueryFilteredPageRequest {

	public static enum SORT_ORDER {
		DESC, ASC
	}

	@ApiModelProperty("Page number")
	private int page;

	@ApiModelProperty("Items per page")
	private int limit;
	@ApiModelProperty("Items per page")
	private SortInfo sort;

	@ApiModelProperty(value = "Query to filter entities. Currently supports eq, lt, gt, ne." +
			" Strings with spaces should be encapsulated into double quotes. Each parenthesis should have spaces before and after",
	example = "( date gt 2019-01-01 or date lt 2019-01-01 ) and name = Alex",
	notes = "Date format: YYYY-MM-DD. Time format: HH-MM")
	private String filter;

	public QueryFilteredPageRequest() {
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public SortInfo getSort() {
		return sort;
	}

	public void setSort(SortInfo sort) {
		this.sort = sort;
	}

	@ApiModel("Sort information for request")
	public static class SortInfo {
		private String field;
		private SORT_ORDER order;

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public SORT_ORDER getOrder() {
			return order;
		}

		public void setOrder(SORT_ORDER order) {
			this.order = order;
		}
	}
}
