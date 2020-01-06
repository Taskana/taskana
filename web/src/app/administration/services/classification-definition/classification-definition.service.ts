import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'app/../environments/environment';
import { ClassificationDefinition } from 'app/models/classification-definition';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { BlobGenerator } from 'app/shared/util/blob-generator';

@Injectable()
export class ClassificationDefinitionService {

  url = `${environment.taskanaRestUrl}/v1/classification-definitions`;
  constructor(private httpClient: HttpClient) { }

  // GET
  async exportClassifications(domain: string) {
    domain = (domain === '' ? '' : `?domain=${domain}`);
    const classificationDefinitions = await this.httpClient.get<ClassificationDefinition[]>(this.url + domain).toPromise();
    BlobGenerator.saveFile(classificationDefinitions, `Classifications_${TaskanaDate.getDate()}.json`)
  }
}
