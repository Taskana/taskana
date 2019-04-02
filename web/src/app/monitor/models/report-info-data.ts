export class ReportInfoData {
  cells: Map<string, number>;
  foldableRows: Map<string, Map<string, ReportInfoData>>;
  total: number;
}

export class ReportInfoDataIterable {
  key: string;
  val: ReportInfoData;
  depth: number;
}
