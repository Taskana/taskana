import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'app/../environments/environment';
import { ClassificationDefinition } from 'app/models/classification-definition';
import { saveAs } from 'file-saver/FileSaver';
import { TaskanaDate } from 'app/shared/util/taskana.date';

@Injectable()
export class ClassificationDefinitionService {

  url = environment.taskanaRestUrl + '/v1/classification-definitions';
  constructor(
    private httpClient: HttpClient
  ) {
  }

  // GET
  exportClassifications(domain: string) {
    domain = (domain === '' ? '' : '?domain=' + domain);
    this.httpClient.get<ClassificationDefinition[]>(this.url + domain)
      .subscribe(
        response => saveAs(new Blob([JSON.stringify(response)], { type: 'text/plain;charset=utf-8' }),
          'Classifications_' + TaskanaDate.getDate() + '.json')
      );
  }
}
