import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'app/../environments/environment';
import { saveAs } from 'file-saver/FileSaver';
import { WorkbasketDefinition } from 'app/models/workbasket-definition';
import { TaskanaDate } from 'app/shared/util/taskana.date';

@Injectable()
export class WorkbasketDefinitionService {
  url: string = environment.taskanaRestUrl + '/v1/workbasket-definitions';

  constructor(private httpClient: HttpClient) {
  }

  // GET
  exportWorkbaskets(domain: string) {
    domain = (domain === '' ? '' : '?domain=' + domain);
    this.httpClient.get<WorkbasketDefinition[]>(this.url + domain).subscribe(
      response => {
        saveAs(new Blob([JSON.stringify(response)], { type: 'text/plain;charset=utf-8' }),
          'Workbaskets_' + TaskanaDate.getDate() + '.json');
      }
    );
  }
}
