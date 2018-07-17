import { Component, OnInit, HostListener } from '@angular/core';
import { opacity } from 'app/shared/animations/expand.animation';

@Component({
  selector: 'taskana-code',
  templateUrl: './code.component.html',
  styleUrls: ['./code.component.scss'],
  animations: [opacity]
})
export class CodeComponent implements OnInit {

  code = 'ArrowUpArrowUpArrowDownArrowDownArrowLeftArrowRightArrowLeftArrowRightKeyBKeyA';
  bufferKeys = '';
  showCode = false;
  images = [0, 1, 2, 3, 4];
  @HostListener('window:keyup', ['$event'])
  keyEvent(event: KeyboardEvent) {
    if (this.bufferKeys === '') {
      setTimeout(() => this.bufferKeys = '', 5000);
    }
    this.bufferKeys += event.code;
    if (this.code === this.bufferKeys) {
      this.showCode = true;
      setTimeout(() => this.showCode = false, 5000);
    }

  }

  constructor() { }

  ngOnInit() {
  }

}
