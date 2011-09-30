/**
 * Copyright 2009-2010 VMware.
 * All rights reserved.
 */

package org.harrison.insight.plugin.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.springsource.insight.idk.AbstractOperationViewTest;
import com.springsource.insight.idk.WebApplicationContextLoader;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationFields;
import org.aspectj.lang.Signature;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertNotNull;


/**
 */
@ContextConfiguration(locations = { "classpath:META-INF/insight-plugin-mongodb.xml",
                                    "classpath:META-INF/test-app-context.xml" },
                      loader = WebApplicationContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class OperationFTLTest extends AbstractOperationViewTest {
    public OperationFTLTest() {
        super(MongoDbOperationCollectionAspect.TYPE);
    }

    @Test
    public void testView() throws Exception {
        JoinPoint jp = mock(JoinPoint.class);
        Signature sig = mock(Signature.class);
        when(jp.getSignature()).thenReturn(sig);
        when(sig.getName()).thenReturn("GreatHeapingMethod");
        when(jp.getArgs()).thenReturn(new String[]{"one", "two"});
        JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart.class);
        SourceLocation sl = mock(SourceLocation.class);
        when(sl.getFileName()).thenReturn("Test.java");
        when(sl.getLine()).thenReturn(100);
        when(sl.getWithinType()).thenReturn(this.getClass());
        when(staticPart.getSourceLocation()).thenReturn(sl);
        MethodSignature msig = mock(MethodSignature.class);
        when(staticPart.getSignature()).thenReturn(msig);
        when(jp.getStaticPart()).thenReturn(staticPart);

        // Db
        Operation op = MongoDbOperationCollectionAspect.aspectOf().createOperation(jp);
        op.put(OperationFields.RETURN_VALUE, "my return");
        String content = getRenderingOf(op);
        assertNotNull(content);

        // Cursor
        DBCursor cursor = mock(DBCursor.class);
        when(cursor.getKeysWanted()).thenReturn(new BasicDBObject("everybody", "loves"));
        when(cursor.getQuery()).thenReturn(new BasicDBObject("spring", "insight"));
        when(jp.getTarget()).thenReturn(cursor);
        op = MongoCursorOperationCollectionAspect.aspectOf().createOperation(jp);
        op.put(OperationFields.RETURN_VALUE, "my return");
        content = getRenderingOf(op);
        assertNotNull(content);

        // Collection
        DBCollection collection = mock(DBCollection.class);
        when(collection.getFullName()).thenReturn("this is a super cool collection");
        when(jp.getThis()).thenReturn(collection);
        op = MongoCollectionOperationCollectionAspect.aspectOf().createOperation(jp);
        op.put(OperationFields.RETURN_VALUE, "my return");
        content = getRenderingOf(op);
        assertNotNull(content);
    }

}