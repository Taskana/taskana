import { trigger, style, transition, animate, keyframes } from '@angular/animations';

export const highlight = trigger('validation', [
  transition(
    'true => false, false => true, * => true',
    animate(
      '1500ms',
      keyframes([
        style({ opacity: '1' }),
        style({ opacity: '0.3' }),
        style({ opacity: '1' }),
        style({ opacity: '0.3' }),
        style({ opacity: '1' }),
        style({ opacity: '0.3' }),
        style({ opacity: '1' })
      ])
    )
  )
]);
