import { Component, DebugElement, ElementRef, Renderer2 } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ResizableWidthDirective } from './resizable-width.directive';

@Component({
  template: ` <div taskanaResizableWidth></div>`
})
class TestComponent {}

describe('ResizableDirective', () => {
  let fixture: ComponentFixture<TestComponent>;
  let inputDebug: DebugElement;
  let inputElement: HTMLInputElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestComponent, ResizableWidthDirective]
    });
    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();

    inputDebug = fixture.debugElement.query(By.directive(ResizableWidthDirective));
    inputElement = inputDebug.nativeElement;
  });

  it('should create an instance', () => {
    const renderer = fixture.componentRef.injector.get<Renderer2>(Renderer2 as any);
    const directive = new ResizableWidthDirective(renderer, new ElementRef(inputElement));

    expect(directive).toBeTruthy();
  });

  it('should handle mouseover', () => {
    inputElement.dispatchEvent(new MouseEvent('mouseover'));

    expect(document.body.style.cursor).toBe('col-resize');
    expect(inputElement.style.userSelect).toBe('none');
  });

  it('should handle mouseout', () => {
    inputElement.dispatchEvent(new MouseEvent('mouseout'));

    expect(document.body.style.cursor).toBe('');
    expect(inputElement.style.userSelect).toBe('');
  });

  it('should resize element after mousedown and mousemove', () => {
    const startPosition: number = 100;
    const endPosition: number = 200;

    const mousedownEvent = new MouseEvent('mousedown', { bubbles: true, cancelable: true });
    Object.defineProperty(mousedownEvent, 'pageX', { value: startPosition });
    inputElement.dispatchEvent(mousedownEvent);

    const initialWidth = inputElement.clientWidth;
    const mousemoveEvent = new MouseEvent('mousemove', { bubbles: true, cancelable: true });
    Object.defineProperty(mousemoveEvent, 'pageX', { value: endPosition });
    inputElement.dispatchEvent(mousemoveEvent);

    const expectedWidth = initialWidth + (endPosition - startPosition);
    expect(inputElement.style.minWidth).toBe(`${expectedWidth}px`);
    expect(inputElement.style.width).toBe(`${expectedWidth}px`);
  });

  it('should not resize element after mouseup', () => {
    const startPosition: number = 100;
    const endPosition: number = 200;

    const mouseupEvent = new MouseEvent('mouseup', { bubbles: true, cancelable: true });
    Object.defineProperty(mouseupEvent, 'pageX', { value: startPosition });
    inputElement.dispatchEvent(mouseupEvent);

    fixture.detectChanges();

    const mousemoveEvent = new MouseEvent('mousemove', { bubbles: true, cancelable: true });
    Object.defineProperty(mousemoveEvent, 'pageX', { value: endPosition });
    inputElement.dispatchEvent(mousemoveEvent);

    expect(inputElement.style.minWidth).toBe('');
    expect(inputElement.style.width).toBe('');
  });
});
