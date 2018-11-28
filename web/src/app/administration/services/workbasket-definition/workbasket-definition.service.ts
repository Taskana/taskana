import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'app/../environments/environment';
import { saveAs } from 'file-saver/FileSaver';
import { AlertService } from 'app/services/alert/alert.service';
import { WorkbasketDefinition } from 'app/models/workbasket-definition';
import { AlertModel, AlertType } from 'app/models/alert';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { ErrorModel } from 'app/models/modal-error';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';


@Injectable()
export class WorkbasketDefinitionService {
  url: string = environment.taskanaRestUrl + '/v1/workbasket-definitions';

  constructor(private httpClient: HttpClient, private alertService: AlertService,
    private errorModalService: ErrorModalService) {
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
