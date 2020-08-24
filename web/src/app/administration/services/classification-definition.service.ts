import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { BlobGenerator } from 'app/shared/util/blob-generator';
import { Classification } from '../../shared/models/classification';
import { StartupService } from '../../shared/services/startup/startup.service';

@Injectable()
export class ClassificationDefinitionService {
  constructor(private httpClient: HttpClient, private startupService: StartupService) {}

  get url(): string {
    return this.startupService.getTaskanaRestUrl() + '/v1/classification-definitions';
  }

  // GET
  async exportClassifications(domain: string) {
    const domainRequest = domain ? '' : `?domain=${domain}`;
    const classificationDefinitions = await this.httpClient.get<Classification[]>(this.url + domainRequest).toPromise();
    BlobGenerator.saveFile(classificationDefinitions, `Classifications_${TaskanaDate.getDate()}.json`);
  }
}
