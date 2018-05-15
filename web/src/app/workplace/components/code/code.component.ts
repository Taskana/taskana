import { Component, OnInit, HostListener } from '@angular/core';
import { trigger, transition, keyframes, style, animate, state } from '@angular/animations';

@Component({
  selector: 'taskana-code',
  templateUrl: './code.component.html',
  styleUrls: ['./code.component.scss'],
  animations: [
    trigger('toggle', [
      state('*', style({ opacity: '1' })),
      state('void', style({ opacity: '0' })),
      transition('void => *', animate('300ms ease-in', keyframes([
        style({ opacity: 0 }),
        style({ opacity: 0.5 }),
        style({ opacity: 1 })]))),
      transition('* => void', animate('300ms ease-out', keyframes([
        style({ opacity: 1 }),
        style({ opacity: 0.5 }),
        style({ opacity: 0 })])))
    ])
  ]
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
