import { ReportMeta } from "./report-meta";

export class ReportModel {
  meta: ReportMeta;
  // The keys of the rows object are unknown. They represent the name of that specific row.
  // Each row (value of rows Object) has two keys: 'cells:Object' and 'total:number'.
  // The keys of 'cells' are the same as 'meta.header:number'.
  // This also applies to sumRow.
  rows: Object;
  sumRow: Object;
}
