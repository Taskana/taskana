import { Component, OnInit } from '@angular/core';
import { RoutingUploadService } from '../routing-upload.service';
import { NotificationService } from '../../shared/services/notifications/notification.service';
import { HotToastService } from '@ngneat/hot-toast';

@Component({
  selector: 'taskana-routing-upload',
  templateUrl: './routing-upload.component.html',
  styleUrls: ['./routing-upload.component.scss']
})
export class RoutingUploadComponent implements OnInit {
  file: File | null = null;

  constructor(
    private routingUploadService: RoutingUploadService,
    private toastService: HotToastService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {}

  onFileChanged(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    this.file = input.files[0];
    this.upload(input.files);
  }

  upload(fileList?: FileList) {
    if (typeof fileList !== 'undefined') {
      this.file = fileList[0];
    }
    this.routingUploadService.uploadRoutingRules(this.file).subscribe({
      next: (res: { amountOfImportedRow: number; result: string }) => this.toastService.success(res.result),
      error: (err) => {
        this.notificationService.showError(err.error.message.key);
        this.clearInput();
      },
      complete: () => this.clearInput()
    });
  }

  clearInput() {
    const inputElement = document.getElementById('routingUpload') as HTMLInputElement;
    inputElement.value = '';
    this.file = null;
  }
}
