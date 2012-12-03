package org.jboss.ee6lab.cdi.wumpus.xmlbeans;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

public class InjectLiteral extends AnnotationLiteral<Inject> implements Inject {
    private static final long serialVersionUID = -5547537708250537848L;

    public static final Inject INSTANCE = new InjectLiteral();
}
