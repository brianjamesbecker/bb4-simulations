// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.geometry;

import com.barrybecker4.simulation.snake.LocomotionParameters;

import javax.vecmath.Vector2d;

import static com.barrybecker4.simulation.snake.geometry.Segment.CENTER_INDEX;
import static com.barrybecker4.simulation.snake.geometry.Segment.NUM_PARTICLES;


/**
 *  Updates a segment forward in time one timestep
 *
 *  @author Barry Becker
 */
public class SegmentUpdater {

    private static final double EPS = 0.00001;

    /** temporary vector to aid in calculations (saves creating a lot of new vector objects)  */
    private Vector2d vel_ = new Vector2d( 0, 0 );
    private Vector2d change_ = new Vector2d( 0, 0 );

    /** constructor */
    public SegmentUpdater() {}


    /**
     * update particle forces
     * look at how much the springs are deflected to determine how much force to apply
     * to each particle. Also include the frictional forces
     */
    public void updateForces(Segment segment) {
        Edge[] edges_ = segment.getEdges();
        Particle[] particles_ = segment.getParticles();

        Vector2d e0Force = edges_[0].getForce();
        Vector2d e1Force = edges_[1].getForce();
        Vector2d e2Force = edges_[2].getForce();
        Vector2d e4Force = edges_[4].getForce();
        Vector2d e5Force = edges_[5].getForce();
        Vector2d e6Force = edges_[6].getForce();
        Vector2d e7Force = edges_[7].getForce();

        // update the front 3 particle forces
        particles_[1].force.set( 0, 0 );
        particles_[1].force.add( e0Force );
        particles_[1].force.sub( e5Force );
        particles_[1].force.sub( e1Force );

        particles_[2].force.set( 0, 0 );
        particles_[2].force.sub( e2Force );
        particles_[2].force.sub( e6Force );
        particles_[2].force.add( e1Force );

        particles_[CENTER_INDEX].force.set( 0, 0 );
        particles_[CENTER_INDEX].force.add( e4Force );
        particles_[CENTER_INDEX].force.add( e7Force );
        particles_[CENTER_INDEX].force.add( e5Force );
        particles_[CENTER_INDEX].force.add( e6Force );

        if ( !segment.isHead() ) {
            Segment segmentInFront = segment.segmentInFront_;
            particles_[1].force.sub(segmentInFront.getRightForce());
            particles_[1].force.sub(segmentInFront.getRightBackDiagForce());

            particles_[2].force.add( segmentInFront.getLeftForce() );
            particles_[2].force.sub(segmentInFront.getLeftBackDiagForce());
        }
        if (segment.isTail()) {
            Vector2d e3Force = edges_[3].getForce();

            // update back 2 particle forces if at tail
            particles_[0].force.set( 0, 0 );
            particles_[0].force.sub( e3Force );
            particles_[0].force.sub( e0Force );
            particles_[0].force.sub( e4Force );

            particles_[3].force.set( 0, 0 );
            particles_[3].force.add( e3Force );
            particles_[3].force.sub( e7Force );
            particles_[3].force.add( e2Force );
        }
    }

    /**
     * update frictional forces acting on particles
     * The coefficient of static friction should be used until the particle
     * is in motion, then the coefficient of dynamic friction is use to determine the
     * frictional force acting in the direction opposite to the velocity.
     * Static friction acts opposite to the force, while dynamic friction is applied
     * opposite the velocity vector.
     * @param params locomotion params controlled by sliders
     */
    public void updateFrictionalForce(Segment segment, LocomotionParameters params)  {
        int i = CENTER_INDEX;
        Particle[] particles_ = segment.getParticles();

        vel_.set( particles_[i].force );
        double forceMag = vel_.length();
        // the frictional force is the weight of the segment (particle mass *3) * coefficient of friction
        double frictionalForce;

        // take into account friction for the center particle
        change_.set( particles_[i].velocity );
        double velMag = change_.length();
        if ( velMag > EPS ) {
            change_.normalize();
            frictionalForce = -particles_[i].mass * params.getDynamicFriction();
            change_.scale( frictionalForce );

            // eliminate the frictional force in the spinal direction
            Vector2d spineDir = segment.getSpinalDirection();
            double dot = spineDir.dot( change_ );
            if ( dot < 0 ) {
                // then the velocity vector is going at least partially backwards
                // remove the backwards component.
                vel_.set( spineDir );
                vel_.scale( dot );
                change_.sub( vel_ );
            }
        }
        else if ( velMag <= EPS && forceMag > EPS ) {
            change_.set( particles_[i].force );
            change_.normalize();
            frictionalForce = -particles_[i].mass * params.getStaticFriction();
            change_.scale( frictionalForce );
        }
        else {
            // velocity and force are both very near 0, so make them both 0
            particles_[i].force.set( 0.0, 0.0 );
            particles_[i].velocity.set( 0.0, 0.0 );
            change_.set( 0.0, 0.0 );
        }

        particles_[i].frictionalForce.set( change_ );
    }

    /**
     * update accelerations of particles
     * recall that F=ma so we can get the acceleration
     * by dividing F by m
     */
    public void updateAccelerations(Segment segment) {

        Particle[] particles_ = segment.getParticles();
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || segment.isTail() ) {
                vel_.set( particles_[i].force );
                vel_.add( particles_[i].frictionalForce );
                vel_.scale( (1.0 / particles_[i].mass) );
                particles_[i].acceleration.set( vel_ );
            }
        }
    }

    /**
     * update velocities of particles by integrating the acceleration
     * recall that this is just  vel0 + acceleration * dt
     *
     * We must also update velocities based taking into account the friction
     * on the bottom of the snake. Only the center particle of the snake segment
     * is in contact with the ground.
     *
     * @return unstable if the velocities are getting to big. This is an indication that we should reduce the timestep.
     */
    public boolean updateVelocities(Segment segment, double timeStep ) {
        Particle[] particles_ = segment.getParticles();
        boolean unstable = false;
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || segment.isTail() ) {
                // the current velocity v0
                vel_.set( particles_[i].velocity );
                change_.set( particles_[i].acceleration );
                change_.scale( timeStep );

                if ( change_.length() > 100.0 ) {
                    //System.out.println("becoming unstable vel mag="+change_.magnitude());
                    unstable = true;
                }
                vel_.add( change_ );
                particles_[i].velocity.set( vel_ );
            }
        }
        return unstable;
    }

    /**
     * move particles according to vector field by integrating the velocity
     * recall that this is just pos0 + velocity * dt
     * and pos = pos0 + velocity * dt + 1/2 acceleration * dt*dt
     * where dt = timeStep
     */
    public void updatePositions(Segment segment, double timeStep ) {

        Particle[] particles_ = segment.getParticles();
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || segment.isTail() ) {
                // the current velocity v0
                vel_.set( particles_[i].x, particles_[i].y );
                change_.set( particles_[i].velocity );
                change_.scale( timeStep );
                vel_.add( change_ );
                particles_[i].set( vel_.x, vel_.y );
            }
        }
    }
}