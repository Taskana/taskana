import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {saveAs} from 'file-saver/FileSaver';
import {WorkbasketDefinition} from '../../model/workbasket-definition';
import {DatePipe} from '@angular/common';
import {AlertModel, AlertService, AlertType} from '../alert.service';


@Injectable()
export class WorkbasketDefinitionService {
  url: string = environment.taskanaRestUrl + '/v1/workbasketdefinitions';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };


  constructor(private httpClient: HttpClient, private alertService: AlertService) {
  }

  // GET
  exportAllWorkbaskets() {
    this.httpClient.get<WorkbasketDefinition[]>(this.url, this.httpOptions).subscribe(
      response => saveAs(new Blob([JSON.stringify(response)], {type: 'text/plain;charset=utf-8'}), this.generateName())
    );
  }

  // GET
  exportWorkbasketsByDomain(domain: string) {
    this.httpClient.get<WorkbasketDefinition[]>(this.url + '?' + 'domain=' + domain, this.httpOptions).subscribe(
      response => {
        saveAs(new Blob([JSON.stringify(response)], {type: 'text/plain;charset=utf-8'}), this.generateName(domain));
        console.log(response);
      }
    );
  }

  // POST
  // TODO handle error
  importWorkbasketDefinitions(workbasketDefinitions: any) {
    console.log('importing workbaskets');
    this.httpClient.post(environment.taskanaRestUrl + '/v1/workbasketdefinitions/import',
      JSON.parse(workbasketDefinitions), this.httpOptions).subscribe(
      workbasketsUpdated => this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, 'Import was successful')),
      error => this.alertService.triggerAlert(new AlertModel(AlertType.DANGER, 'Import was not successful'))
    );
  }

  private generateName(domain = ''): string {
    const dateFormat = 'yyyy-MM-ddTHH:mm:ss';
    const dateLocale = 'en-US';
    const datePipe = new DatePipe(dateLocale);
    const date = datePipe.transform(Date.now(), dateFormat) + 'Z';
    return 'Workbaskets_' + date + '.json';
  }

}
