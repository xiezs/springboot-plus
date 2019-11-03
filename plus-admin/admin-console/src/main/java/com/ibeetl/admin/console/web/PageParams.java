package com.ibeetl.admin.console.web;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PageParams {
  @NotNull Integer page = 1;
  @NotNull Integer limit = 10;
}
