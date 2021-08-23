import { Component, OnInit } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'taskana-routing-upload',
  templateUrl: './routing-upload.component.html',
  styleUrls: ['./routing-upload.component.scss']
})
export class RoutingUploadComponent implements OnInit {
  file: File | null = null;
  image: SafeUrl;

  constructor() {}

  ngOnInit(): void {}

  onFileChanged(event: Event) {
    const input = event.target as HTMLInputElement;

    if (!input.files?.length) return;

    this.file = input.files[0];
    this.upload();
  }

  upload(fileList?: FileList) {
    console.log(fileList);
    if (typeof fileList != 'undefined') {
      this.file = fileList[0];
    }
    //do upload
  }
}
