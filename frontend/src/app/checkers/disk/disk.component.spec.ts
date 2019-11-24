import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DiskComponent} from './disk.component';
import {Disk} from "../domain/disk";

const disk: Disk = {id: 'id', color: 'BLACK', king: false, state: 'IN_PLAY', currentPosition: {x: 0, y: 0}};

describe('DiskComponent', () => {
  let component: DiskComponent;
  let fixture: ComponentFixture<DiskComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DiskComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DiskComponent);
    component = fixture.componentInstance;
    component.disk = disk;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have class disk-BLACK', () => {
    component.disk.king = false;
    fixture.detectChanges();

    const element = fixture.debugElement.nativeElement.querySelector('.disk');
    const imageElement = fixture.debugElement.nativeElement.querySelector('img.king');

    expect(imageElement).toBeNull();
    expect(element).toHaveClass('disk');
  });

  it('should have have a crown image if is king disk', () => {
    component.disk.king = true;
    fixture.detectChanges();

    const imageElement = fixture.debugElement.nativeElement.querySelector('img.king');
    expect(imageElement).toBeTruthy();
  });
});
