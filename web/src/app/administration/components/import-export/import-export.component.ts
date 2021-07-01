import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition.service';
import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition.service';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { TaskanaType } from 'app/shared/models/taskana-type';
import { environment } from 'environments/environment';
import { UploadService } from 'app/shared/services/upload/upload.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';

@Component({
  selector: 'taskana-administration-import-export',
  templateUrl: './import-export.component.html',
  styleUrls: ['./import-export.component.scss']
})
export class ImportExportComponent implements OnInit {
  @Input() currentSelection: TaskanaType;
  @Input() parentComponent: string;

  @ViewChild('selectedFile', { static: true })
  selectedFileInput;

  domains: string[] = [];

  constructor(
    private domainService: DomainService,
    private workbasketDefinitionService: WorkbasketDefinitionService,
    private classificationDefinitionService: ClassificationDefinitionService,
    public uploadService: UploadService,
    private notificationService: NotificationService,
    private importExportService: ImportExportService
  ) {}

  ngOnInit() {
    this.domainService.getDomains().subscribe((data) => {
      this.domains = data;
    });
  }

  export(domain = '') {
    if (this.currentSelection === TaskanaType.WORKBASKETS) {
      this.workbasketDefinitionService.exportWorkbaskets(domain);
    } else {
      this.classificationDefinitionService.exportClassifications(domain);
    }
  }

  uploadFile() {
    const file = this.selectedFileInput.nativeElement.files[0];
    const formdata = new FormData();
    const ajax = new XMLHttpRequest();
    if (this.checkFormatFile(file)) {
      formdata.append('file', file);
      ajax.upload.addEventListener('progress', this.progressHandler.bind(this), false);
      ajax.addEventListener('load', this.resetProgress.bind(this), false);
      ajax.addEventListener('error', this.onFailedResponse.bind(this, ajax), false);
      ajax.onreadystatechange = this.onReadyStateChangeHandler.bind(this, ajax);
      if (this.currentSelection === TaskanaType.WORKBASKETS) {
        ajax.open('POST', `${environment.taskanaRestUrl}/v1/workbasket-definitions`);
      } else {
        ajax.open('POST', `${environment.taskanaRestUrl}/v1/classification-definitions`);
      }
      if (!environment.production) {
        ajax.setRequestHeader('Authorization', 'Basic YWRtaW46YWRtaW4=');
      }
      ajax.send(formdata);
      this.uploadService.isInUse = true;
      this.uploadService.setCurrentProgressValue(1);
    }
  }

  progressHandler(event) {
    const percent = (event.loaded / event.total) * 100;
    this.uploadService.setCurrentProgressValue(Math.round(percent));
  }

  private checkFormatFile(file): boolean {
    const ending = file.name.match(/\.([^.]+)$/)[1];
    let check = false;
    if (ending === 'json') {
      check = true;
    } else {
      file.value = '';
      this.notificationService.showError('IMPORT_EXPORT_UPLOAD_FILE_FORMAT');
    }
    return check;
  }

  private resetProgress() {
    this.uploadService.setCurrentProgressValue(0);
    this.uploadService.isInUse = false;
    this.selectedFileInput.nativeElement.value = '';
  }

  private onReadyStateChangeHandler(event) {
    if (event.readyState === 4 && event.status >= 400) {
      let key = 'FALLBACK';

      if (event.status === 401) {
        key = 'IMPORT_EXPORT_UPLOAD_FAILED_AUTH';
      } else if (event.status === 404) {
        key = 'IMPORT_EXPORT_UPLOAD_FAILED_NOT_FOUND';
      } else if (event.status === 409) {
        key = 'IMPORT_EXPORT_UPLOAD_FAILED_CONFLICTS';
      } else if (event.status === 413) {
        key = 'IMPORT_EXPORT_UPLOAD_FAILED_SIZE';
      }
      this.errorHandler(key);
    } else if (event.readyState === 4 && event.status === 204) {
      const message = this.currentSelection === TaskanaType.WORKBASKETS ? 'WORKBASKET_IMPORT' : 'CLASSIFICATION_IMPORT';
      this.notificationService.showSuccess(message);
      this.importExportService.setImportingFinished(true);
      this.resetProgress();
    }
  }

  private onFailedResponse() {
    this.errorHandler('IMPORT_EXPORT_UPLOAD_FAILED');
  }

  private errorHandler(key: string) {
    this.notificationService.showError(key);
    delete this.selectedFileInput.files;
    this.resetProgress();
  }
}
