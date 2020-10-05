import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TaskanaType } from '../../../shared/models/taskana-type';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { WorkbasketDefinitionService } from '../../services/workbasket-definition.service';
import { ClassificationDefinitionService } from '../../services/classification-definition.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { UploadService } from '../../../shared/services/upload/upload.service';
import { ImportExportService } from '../../services/import-export.service';
import { WorkbasketDefinition } from '../../../shared/models/workbasket-definition';
import { Classification } from '../../../shared/models/classification';
import { environment } from '../../../../environments/environment';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';

@Component({
  selector: 'taskana-administration-import-export',
  templateUrl: './import-export.component.html',
  styleUrls: ['./import-export.component.scss']
})
export class ImportExportComponent implements OnInit {
  @Input() currentSelection: TaskanaType;

  @ViewChild('selectedFile', { static: true })
  selectedFileInput;

  domains: string[] = [];
  errorWhileUploadingText: string;

  constructor(
    private domainService: DomainService,
    private workbasketDefinitionService: WorkbasketDefinitionService,
    private classificationDefinitionService: ClassificationDefinitionService,
    private notificationsService: NotificationService,
    public uploadService: UploadService,
    private errorsService: NotificationService,
    private importExportService: ImportExportService
  ) {}

  ngOnInit() {
    this.domainService.getDomains().subscribe((data) => {
      this.domains = data;
    });
  }

  export(domain = ''): Observable<WorkbasketDefinition[] | Classification[]> {
    if (this.currentSelection === TaskanaType.WORKBASKETS) {
      return this.workbasketDefinitionService.exportWorkbaskets(domain);
    } else {
      return this.classificationDefinitionService.exportClassifications(domain);
    }
  }

  uploadFile() {
    const file = this.selectedFileInput.nativeElement.files[0];
    const formData = new FormData();
    const xhr = new XMLHttpRequest();
    if (this.checkFormatFile(file)) {
      formData.append('file', file);
      xhr.upload.addEventListener('progress', this.progressHandler.bind(this), false);
      xhr.addEventListener('load', this.resetProgress.bind(this), false);
      xhr.addEventListener('error', this.onFailedResponse.bind(this, xhr), false);
      xhr.onreadystatechange = this.onReadyStateChangeHandler.bind(this, xhr);
      if (this.currentSelection === TaskanaType.WORKBASKETS) {
        xhr.open('POST', `${environment.taskanaRestUrl}/v1/workbasket-definitions`);
      } else {
        xhr.open('POST', `${environment.taskanaRestUrl}/v1/classification-definitions`);
      }
      if (!environment.production) {
        xhr.setRequestHeader('Authorization', 'Basic YWRtaW46YWRtaW4=');
      }
      xhr.send(formData);
      this.uploadService.isInUse = true;
      this.uploadService.setCurrentProgressValue(1);
    }
  }

  progressHandler(event) {
    const percent = (event.loaded / event.total) * 100;
    this.uploadService.setCurrentProgressValue(Math.round(percent));
  }

  private checkFormatFile(file): boolean {
    if (file.name.endsWith('json')) {
      return true;
    } else {
      file.value = '';
      this.errorsService.triggerError(NOTIFICATION_TYPES.FILE_ERR);
      return false;
    }
  }

  private resetProgress() {
    this.uploadService.setCurrentProgressValue(0);
    this.uploadService.isInUse = false;
    this.selectedFileInput.nativeElement.value = '';
  }

  private onReadyStateChangeHandler(event) {
    if (event.readyState === 4 && event.status >= 400) {
      let key: NOTIFICATION_TYPES;
      if (event.status === 401) {
        key = NOTIFICATION_TYPES.IMPORT_ERR_1;
      } else if (event.status === 404) {
        key = NOTIFICATION_TYPES.IMPORT_ERR_2;
      } else if (event.status === 409) {
        key = NOTIFICATION_TYPES.IMPORT_ERR_3;
      } else if (event.status === 413) {
        key = NOTIFICATION_TYPES.IMPORT_ERR_4;
      }
      this.errorHandler(key, event);
    } else if (event.readyState === 4 && event.status === 200) {
      this.notificationsService.showToast(NOTIFICATION_TYPES.SUCCESS_ALERT_6);
      this.importExportService.setImportingFinished(true);
      this.resetProgress();
    }
  }

  private onFailedResponse() {
    this.errorHandler(NOTIFICATION_TYPES.UPLOAD_ERR);
  }

  private errorHandler(key: NOTIFICATION_TYPES, passedError?: HttpErrorResponse) {
    this.errorsService.triggerError(key, passedError);
    delete this.selectedFileInput.files;
    this.resetProgress();
  }
}
