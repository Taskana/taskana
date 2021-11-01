import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { BlobGenerator } from 'app/shared/util/blob-generator';
import { Classification } from '../../shared/models/classification';
import { StartupService } from '../../shared/services/startup/startup.service';
import { take } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable()
export class ClassificationDefinitionService {
  constructor(private httpClient: HttpClient, private startupService: StartupService) {}

  get url(): string {
    return this.startupService.getTaskanaRestUrl() + '/v1/classification-definitions';
  }

  // GET
  exportClassifications(domain: string): Observable<Classification[]> {
    const domainRequest = domain ? '' : `?domain=${domain}`;
    const classificationDefObservable = this.httpClient.get<Classification[]>(this.url + domainRequest).pipe(take(1));
    classificationDefObservable.subscribe((classificationDefinitions) =>
      BlobGenerator.saveFile(classificationDefinitions, `Classifications_${TaskanaDate.getDate()}.json`)
    );
    return classificationDefObservable;
  }

  importClassification(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    const headers = new HttpHeaders().set('Content-Type', 'multipart/form-data');
    return this.httpClient.post(this.url, formData, { headers });
  }
}
