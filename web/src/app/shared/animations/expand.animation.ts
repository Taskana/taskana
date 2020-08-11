import { trigger, style, transition, animate, keyframes, state } from '@angular/animations';

export const expandDown = trigger('toggleDown', [
  state('true', style({ opacity: '1', display: 'initial' })),
  state('false', style({ opacity: '0', display: 'none' })),
  transition(
    'false => true',
    animate(
      '300ms ease-in',
      keyframes([
        style({ opacity: 0, height: '0px' }),
        style({ opacity: 0.5, height: '50px' }),
        style({ opacity: 1, height: '*' })
      ])
    )
  ),
  transition(
    'true => false',
    animate(
      '300ms ease-out',
      keyframes([
        style({ opacity: 1, height: '*' }),
        style({ opacity: 0.5, height: '50px' }),
        style({ opacity: 0, height: '0px' })
      ])
    )
  )
]);

export const expandRight = trigger('toggleRight', [
  transition(
    'void => *',
    animate(
      '300ms ease-in',
      keyframes([
        style({ opacity: 0, width: '0px' }),
        style({ opacity: 1, width: '150px' }),
        style({ opacity: 1, width: '*' })
      ])
    )
  ),
  transition(
    '* => void',
    animate(
      '300ms ease-out',
      keyframes([
        style({ opacity: 1, width: '*' }),
        style({ opacity: 0, width: '150px' }),
        style({ opacity: 0, width: '0px' })
      ])
    )
  )
]);

export const expandTop = trigger('toggleTop', [
  state('in', style({ transform: 'translateY(0)', overflow: 'hidden' })),
  transition('void => *', [style({ transform: 'translateY(100%)', overflow: 'hidden' }), animate(100)]),
  transition('* => void', [animate(100, style({ transform: 'translateY(100%)', overflow: 'hidden' }))])
]);

export const opacity = trigger('toggleOpacity', [
  state('*', style({ opacity: '1' })),
  state('void', style({ opacity: '0' })),
  transition(
    'void => *',
    animate('300ms ease-in', keyframes([style({ opacity: 0 }), style({ opacity: 0.5 }), style({ opacity: 1 })]))
  ),
  transition(
    '* => void',
    animate('300ms ease-out', keyframes([style({ opacity: 1 }), style({ opacity: 0.5 }), style({ opacity: 0 })]))
  )
]);
