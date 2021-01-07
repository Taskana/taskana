import { Pipe, PipeTransform } from '@angular/core';
import { WorkbasketSummary } from '../models/workbasket-summary';
import { Side } from '../../administration/components/workbasket-distribution-targets/workbasket-distribution-targets.component';

@Pipe({ name: 'selectWorkbaskets' })
export class SelectWorkBasketPipe implements PipeTransform {
  transform(
    allWorkbaskets: WorkbasketSummary[],
    selectedWorkbaskets: WorkbasketSummary[],
    side: Side
  ): WorkbasketSummary[] {
    if (!allWorkbaskets || !selectedWorkbaskets) {
      return [];
    }
    if (side === Side.SELECTED) {
      return selectedWorkbaskets;
    }
    const selectedWorkbasketIds: string[] = selectedWorkbaskets.map((wb) => wb.workbasketId);
    const isNotASelectedWorkbasket = (wb: WorkbasketSummary) => !selectedWorkbasketIds.includes(wb.workbasketId);
    return allWorkbaskets.filter(isNotASelectedWorkbasket);
  }
}
