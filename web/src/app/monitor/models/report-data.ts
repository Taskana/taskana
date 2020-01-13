import { ReportRow } from './report-row';
import { MetaInfoData } from './meta-info-data';

export class ReportData {
  meta: MetaInfoData;
  rows: Array<ReportRow>;
  sumRow: Array<ReportRow>;
}
