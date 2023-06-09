import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FiliereService } from '../service/filiere.service';
import { IFiliere, Filiere } from '../filiere.model';

import { FiliereUpdateComponent } from './filiere-update.component';

describe('Filiere Management Update Component', () => {
  let comp: FiliereUpdateComponent;
  let fixture: ComponentFixture<FiliereUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let filiereService: FiliereService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FiliereUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FiliereUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FiliereUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    filiereService = TestBed.inject(FiliereService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const filiere: IFiliere = { id: 456 };

      activatedRoute.data = of({ filiere });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(filiere));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Filiere>>();
      const filiere = { id: 123 };
      jest.spyOn(filiereService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ filiere });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: filiere }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(filiereService.update).toHaveBeenCalledWith(filiere);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Filiere>>();
      const filiere = new Filiere();
      jest.spyOn(filiereService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ filiere });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: filiere }));
      saveSubject.complete();

      // THEN
      expect(filiereService.create).toHaveBeenCalledWith(filiere);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Filiere>>();
      const filiere = { id: 123 };
      jest.spyOn(filiereService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ filiere });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(filiereService.update).toHaveBeenCalledWith(filiere);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
