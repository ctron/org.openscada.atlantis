/**
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 * 
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 * 
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */
package org.openscada.common.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.openscada.common.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.openscada.common.CommonPackage
 * @generated
 */
public class CommonAdapterFactory extends AdapterFactoryImpl
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.";

    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static CommonPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CommonAdapterFactory ()
    {
        if ( modelPackage == null )
        {
            modelPackage = CommonPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType ( Object object )
    {
        if ( object == modelPackage )
        {
            return true;
        }
        if ( object instanceof EObject )
        {
            return ( (EObject)object ).eClass ().getEPackage () == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CommonSwitch<Adapter> modelSwitch = new CommonSwitch<Adapter> () {
        @Override
        public Adapter caseAttributesType ( AttributesType object )
        {
            return createAttributesTypeAdapter ();
        }

        @Override
        public Adapter caseAttributeType ( AttributeType object )
        {
            return createAttributeTypeAdapter ();
        }

        @Override
        public Adapter caseVariantBooleanType ( VariantBooleanType object )
        {
            return createVariantBooleanTypeAdapter ();
        }

        @Override
        public Adapter caseVariantDoubleType ( VariantDoubleType object )
        {
            return createVariantDoubleTypeAdapter ();
        }

        @Override
        public Adapter caseVariantInt32Type ( VariantInt32Type object )
        {
            return createVariantInt32TypeAdapter ();
        }

        @Override
        public Adapter caseVariantInt64Type ( VariantInt64Type object )
        {
            return createVariantInt64TypeAdapter ();
        }

        @Override
        public Adapter caseVariantNullType ( VariantNullType object )
        {
            return createVariantNullTypeAdapter ();
        }

        @Override
        public Adapter caseVariantType ( VariantType object )
        {
            return createVariantTypeAdapter ();
        }

        @Override
        public Adapter defaultCase ( EObject object )
        {
            return createEObjectAdapter ();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter ( Notifier target )
    {
        return modelSwitch.doSwitch ( (EObject)target );
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.common.AttributesType <em>Attributes Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.common.AttributesType
     * @generated
     */
    public Adapter createAttributesTypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.common.AttributeType <em>Attribute Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.common.AttributeType
     * @generated
     */
    public Adapter createAttributeTypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.common.VariantBooleanType <em>Variant Boolean Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.common.VariantBooleanType
     * @generated
     */
    public Adapter createVariantBooleanTypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.common.VariantDoubleType <em>Variant Double Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.common.VariantDoubleType
     * @generated
     */
    public Adapter createVariantDoubleTypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.common.VariantInt32Type <em>Variant Int32 Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.common.VariantInt32Type
     * @generated
     */
    public Adapter createVariantInt32TypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.common.VariantInt64Type <em>Variant Int64 Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.common.VariantInt64Type
     * @generated
     */
    public Adapter createVariantInt64TypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.common.VariantNullType <em>Variant Null Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.common.VariantNullType
     * @generated
     */
    public Adapter createVariantNullTypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.common.VariantType <em>Variant Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.common.VariantType
     * @generated
     */
    public Adapter createVariantTypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter ()
    {
        return null;
    }

} //CommonAdapterFactory
