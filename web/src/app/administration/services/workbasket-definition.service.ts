import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { WorkbasketDefinition } from 'app/shared/models/workbasket-definition';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { BlobGenerator } from 'app/shared/util/blob-generator';
import { take } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable()
export class WorkbasketDefinitionService {
  url: string = `${environment.taskanaRestUrl}/v1/workbasket-definitions`;

  constructor(private httpClient: HttpClient) {}

  // GET
  exportWorkbaskets(domain: string): Observable<WorkbasketDefinition[]> {
    const domainRequest = domain === '' ? domain : `?domain=${domain}`;
    const workbasketDefObservable = this.httpClient.get<WorkbasketDefinition[]>(this.url + domainRequest).pipe(take(1));
    workbasketDefObservable.subscribe((workbasketDefinitions) =>
      BlobGenerator.saveFile(workbasketDefinitions, `Workbaskets_${TaskanaDate.getDate()}.json`)
    );
    return workbasketDefObservable;
  }
}
