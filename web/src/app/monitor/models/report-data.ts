import { ReportInfoData } from './report-info-data';
import { MetaInfoData } from './meta-info-data';

export class ReportData {
    meta: MetaInfoData;
    rows: Map<string, ReportInfoData>;
    sumRow: ReportInfoData;
}
