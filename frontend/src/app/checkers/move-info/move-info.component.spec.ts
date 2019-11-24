import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MoveInfoComponent} from './move-info.component';
import {MatCardModule} from "@angular/material/card";
import {GameTimerComponent} from "../game-timer/game-timer.component";

describe('MoveInfoComponent', () => {
  let component: MoveInfoComponent;
  let fixture: ComponentFixture<MoveInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        MoveInfoComponent,
        GameTimerComponent,
      ],
      imports: [
        MatCardModule,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MoveInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
