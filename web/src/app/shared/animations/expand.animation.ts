import { trigger, style, transition, animate, keyframes, state } from '@angular/core';

export const expandDown =
    trigger('toggleDown', [
        state('*', style({ opacity: '1' })),
        state('void', style({ opacity: '0' })),
        transition('void => *', animate('300ms ease-in', keyframes([
            style({ opacity: 0, height: '0px' }),
            style({ opacity: 0.5, height: '50px' }),
            style({ opacity: 1, height: '*' })]))),
        transition('* => void', animate('300ms ease-out', keyframes([
            style({ opacity: 1, height: '*' }),
            style({ opacity: 0.5, height: '50px' }),
            style({ opacity: 0, height: '0px' })])))
    ]);


export const expandRight = trigger('toggleRight', [
    transition('void => *', animate('300ms ease-in', keyframes([
        style({ opacity: 0, width: '0px' }),
        style({ opacity: 1, width: '150px' }),
        style({ opacity: 1, width: '*' })]))),
    transition('* => void', animate('300ms ease-out', keyframes([
        style({ opacity: 1, width: '*' }),
        style({ opacity: 0, width: '150px' }),
        style({ opacity: 0, width: '0px' })])))
]);

export const expandTop = trigger('toggleTop', [
    state('in', style({ transform: 'translateY(0)', overflow: 'hidden' })),
    transition('void => *', [
        style({ transform: 'translateY(100%)', overflow: 'hidden' }),
        animate(100)
    ]),
    transition('* => void', [
        animate(100, style({ transform: 'translateY(100%)', overflow: 'hidden' }))
    ])
])

export const opacity = trigger('toggleOpacity', [
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
